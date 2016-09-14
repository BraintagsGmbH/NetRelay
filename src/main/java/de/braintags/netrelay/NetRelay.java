/*
 * #%L
 * netrelay
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.netrelay;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.init.IDataStoreInit;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mongo.init.MongoDataStoreInit;
import de.braintags.io.vertx.util.exception.InitException;
import de.braintags.io.vertx.util.security.CertificateHelper;
import de.braintags.netrelay.controller.BodyController;
import de.braintags.netrelay.controller.CookieController;
import de.braintags.netrelay.controller.FailureController;
import de.braintags.netrelay.controller.FavIconController;
import de.braintags.netrelay.controller.SessionController;
import de.braintags.netrelay.controller.StaticController;
import de.braintags.netrelay.controller.TimeoutController;
import de.braintags.netrelay.init.MailClientSettings;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.mapping.NetRelayMapperFactory;
import de.braintags.netrelay.processor.ProcessorDefinition;
import de.braintags.netrelay.routing.RouterDefinition;
import de.braintags.netrelay.routing.RoutingInit;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.web.Router;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class NetRelay extends AbstractVerticle {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(NetRelay.class);

  // to be able to handle multiple datastores, an IDatastoreCollection will come from pojo-mapper later
  private IDataStore datastore;
  private Settings settings;
  private Router router;
  private MailClient mailClient;
  /**
   * The mapper factory which translates between the browser and the server
   */
  private IMapperFactory mapperFactory;

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.AbstractVerticle#start()
   */
  @Override
  public void start(Future<Void> startFuture) {
    try {
      settings = initSettings();
      initDataStore(dsInitResult -> {
        if (dsInitResult.failed()) {
          startFuture.fail(dsInitResult.cause());
        } else {
          init(initResult -> {
            if (initResult.failed()) {
              startFuture.fail(initResult.cause());
            } else {
              initComplete(startFuture);
            }
          });
        }
      });
    } catch (Exception e) {
      startFuture.fail(e);
    }
  }

  /**
   * Initialize all needed resources of NetRelay
   * 
   * @param handler
   */
  protected void init(Handler<AsyncResult<Void>> handler) {
    try {
      router = Router.router(vertx);
      mapperFactory = new NetRelayMapperFactory(this);
      initMailClient();
      initController(router);
      initProcessors();
      initHttpServer(router, res -> {
        if (res.failed()) {
          handler.handle(Future.failedFuture(res.cause()));
        } else {
          initHttpsServer(router, httpsResult -> {
            if (httpsResult.failed()) {
              handler.handle(Future.failedFuture(httpsResult.cause()));
            } else {
              handler.handle(Future.succeededFuture());
            }
          });
        }
      });
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  /**
   * Set the future to be completed
   * 
   * @param startFuture
   */
  protected void initComplete(Future<Void> startFuture) {
    startFuture.complete();
  }

  private void initMailClient() {
    MailClientSettings ms = settings.getMailClientSettings();
    initMailClientSettings(ms);
    if (ms.isActive()) {
      mailClient = MailClient.createShared(vertx, ms, ms.getName());
      LOGGER.info("MailClient startet with configuration " + ms.toJson());
    } else {
      LOGGER.info("MailClient NOT started, cause not activated in configuration");
    }
  }

  private void initMailClientSettings(MailClientSettings ms) {
    String mailUserName = System.getProperty(MailClientSettings.USERNAME_SYS_PROPERTY);
    if (mailUserName != null && mailUserName.hashCode() != 0) {
      ms.setUsername(mailUserName);
    }
    String mailClientPassword = System.getProperty(MailClientSettings.PASSWORD_SYS_PROPERTY);
    if (mailClientPassword != null && mailClientPassword.hashCode() != 0) {
      ms.setPassword(mailClientPassword);
    }
    String mailClientHost = System.getProperty(MailClientSettings.HOST_SYS_PROPERTY);
    if (mailClientHost != null && mailClientHost.hashCode() != 0) {
      ms.setHostname(mailClientHost);
    }

    String mailClientPort = System.getProperty(MailClientSettings.PORT_SYS_PROPERTY);
    if (mailClientPort != null && mailClientPort.hashCode() != 0) {
      ms.setPort(Integer.parseInt(mailClientPort));
    }

  }

  /**
   * Retrive the {@link IMapperFactory} which translates between the mappers and the browser
   * 
   * @return the {@link IMapperFactory} of NetRelay
   */
  public IMapperFactory getNetRelayMapperFactory() {
    return mapperFactory;
  }

  /**
   * Get the router, which is used by NetRelay
   * 
   * @return the router
   */
  public Router getRouter() {
    return router;
  }

  /**
   * Init the definitions inside {@link Settings#getProcessorDefinitons()}
   * 
   * @throws Exception
   */
  protected void initProcessors() {
    List<ProcessorDefinition> rd = settings.getProcessorDefinitons().getProcessorDefinitions();
    for (ProcessorDefinition def : rd) {
      def.initProcessorDefinition(vertx, this);
    }
  }

  /**
   * Init the definitions inside {@link Settings#getRouterDefinitions()}
   * 
   * @throws Exception
   */
  protected void initController(Router router) throws Exception {
    List<RouterDefinition> rd = settings.getRouterDefinitions().getRouterDefinitions();
    for (RouterDefinition def : rd) {
      RoutingInit.initRoutingDefinition(vertx, this, router, def);
    }
  }

  /**
   * Initialize the {@link Settings} which are used to init the current instance
   * 
   * @return
   */
  protected Settings initSettings() {
    try {
      Settings st = Settings.loadSettings(this, vertx, context);
      if (!st.isEdited()) {
        throw new InitException(
            "The settings are not yet edited. Change the value of property 'edited' to true inside the appropriate file");
      }
      return st;
    } catch (Exception e) {
      LOGGER.error("", e);
      throw e;
    }
  }

  /**
   * Create an instance of {@link IDataStore} which will be used by the current instance of NetRelay.
   * The init is performed by using the {@link Settings#getDatastoreSettings()}
   * 
   * @handler the handler to be informed
   * @return the {@link IDataStore} to be used
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public final void initDataStore(Handler<AsyncResult<Void>> handler)
      throws InstantiationException, IllegalAccessException {
    IDataStoreInit dsInit = settings.getDatastoreSettings().getDatastoreInit().newInstance();
    dsInit.initDataStore(vertx, settings.getDatastoreSettings(), dsInitResult -> {
      if (dsInitResult.failed()) {
        handler.handle(Future.failedFuture(dsInitResult.cause()));
      } else {
        datastore = dsInitResult.result();
        handler.handle(Future.succeededFuture());
      }
    });
  }

  private void initHttpServer(Router router, Handler<AsyncResult<Void>> handler) {
    HttpServerOptions options = new HttpServerOptions().setPort(settings.getServerPort());
    HttpServer server = vertx.createHttpServer(options);
    server.requestHandler(router::accept).listen(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture());
      }
    });
  }

  private void initHttpsServer(Router router, Handler<AsyncResult<Void>> handler) {
    if (settings.getSslPort() > 0) {
      LOGGER.info("launching ssl server listening on port " + settings.getSslPort());
      HttpServerOptions options = new HttpServerOptions().setPort(settings.getSslPort());
      options.setSsl(true);
      try {
        handleSslCertificate(options, handler);
        HttpServer server = vertx.createHttpServer(options);
        server.requestHandler(router::accept).listen(result -> {
          if (result.failed()) {
            handler.handle(Future.failedFuture(result.cause()));
          } else {
            handler.handle(Future.succeededFuture());
          }
        });
      } catch (Exception e) {
        handler.handle(Future.failedFuture(e));
      }
    } else {
      LOGGER.info("no ssl server is launched, cause ssl port is not set: " + settings.getSslPort());
      handler.handle(Future.succeededFuture());
    }
  }

  private void handleSslCertificate(HttpServerOptions options, Handler<AsyncResult<Void>> handler)
      throws GeneralSecurityException, IOException {
    if (settings.isCertificateSelfSigned()) {
      String password = validateSslPassword();
      CertificateHelper.createSelfCertificate(options, settings.getHostName(), password);
    } else if (settings.getCertificatePath() != null && settings.getCertificatePath().hashCode() != 0) {
      importCertificate(options);
    } else {
      handler.handle(Future.failedFuture(new UnsupportedOperationException(
          "ssl port is set, but no certificate path set and option certificateSelfSigned is not activated")));
    }
  }

  private String validateSslPassword() {
    if (settings.getCertificatePassword() == null || settings.getCertificatePassword().hashCode() == 0) {
      throw new IllegalArgumentException("The property 'certificatePassword' must be set in the settings of NetRelay");
    }
    return settings.getCertificatePassword();
  }

  private void importCertificate(HttpServerOptions httpOpts) {
    String certPath = settings.getCertificatePath();
    String password = settings.getCertificatePassword();

    if (certPath.matches("^.*\\.(pem|PEM)$")) {
      // Use a PEM key/cert pair
      if (settings.getCertificateKeyPath() == null) {
        throw new IllegalArgumentException("The certificateKeyPath is not set for pem certificate");
      }
      httpOpts.setPemKeyCertOptions(
          new PemKeyCertOptions().setCertPath(certPath).setKeyPath(settings.getCertificateKeyPath()));
      httpOpts.setSsl(true);
    } else {
      throw new IllegalArgumentException("Please specify the certificate as PEM file in the format pkcs8");
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.AbstractVerticle#stop(io.vertx.core.Future)
   */
  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    getDatastore().shutdown(result -> {
      if (result.failed()) {
        stopFuture.fail(new RuntimeException(result.cause()));
      } else {
        stopFuture.complete();
      }
    });
  }

  /**
   * The default instance is requested, when there was no saved instance found
   * 
   * @return
   */
  public Settings createDefaultSettings() {
    Settings st = new Settings();
    addDefaultRouterDefinitions(st);
    addDefaultProcessorDefinitions(st);
    st.setDatastoreSettings(MongoDataStoreInit.createDefaultSettings());
    return st;
  }

  protected void addDefaultProcessorDefinitions(Settings settings) {
    ProcessorDefinition def = new ProcessorDefinition();
    def.setActive(false);
    def.setName("dummyprocessor");
    def.getProcessorProperties().put("demoKey", "demoValue");
    def.setTimeDef("60000");
    settings.getProcessorDefinitons().add(def);
  }

  protected void addDefaultRouterDefinitions(Settings settings) {
    settings.getRouterDefinitions().add(FavIconController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(CookieController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(SessionController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(TimeoutController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(BodyController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(StaticController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(FailureController.createDefaultRouterDefinition());
  }

  /**
   * Get the {@link IDataStore} for the current instance
   * 
   * @return the datastore
   */
  public final IDataStore getDatastore() {
    return datastore;
  }

  /**
   * Get the {@link Settings} which are configuring NetRelay
   * 
   * @return the settings
   */
  public Settings getSettings() {
    return settings;
  }

  /**
   * Resets and rebuilds the routes by using the {@link Settings#getRouterDefinitions()}
   * 
   * @throws Exception
   */
  public void resetRoutes() throws Exception {
    getRouter().clear();
    initController(router);
  }

  /**
   * If {@link MailClientSettings#isActive()} from the {@link Settings}, then this will return
   * the initialized instance of {@link MailClient}
   * 
   * @return the mailClient
   */
  public final MailClient getMailClient() {
    return mailClient;
  }
}

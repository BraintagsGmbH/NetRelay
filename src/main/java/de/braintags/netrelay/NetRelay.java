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

import org.apache.commons.lang3.StringUtils;

import de.braintags.netrelay.controller.BodyController;
import de.braintags.netrelay.controller.CookieController;
import de.braintags.netrelay.controller.FailureController;
import de.braintags.netrelay.controller.FavIconController;
import de.braintags.netrelay.controller.SessionController;
import de.braintags.netrelay.controller.StaticController;
import de.braintags.netrelay.controller.TimeoutController;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.mapping.NetRelayMapperFactory;
import de.braintags.netrelay.mapping.NetRelayStoreObjectFactory;
import de.braintags.netrelay.processor.ProcessorDefinition;
import de.braintags.netrelay.routing.RouterDefinition;
import de.braintags.netrelay.routing.RoutingInit;
import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.init.IDataStoreInit;
import de.braintags.vertx.jomnigate.mapping.IMapperFactory;
import de.braintags.vertx.jomnigate.mongo.init.MongoDataStoreInit;
import de.braintags.vertx.util.exception.InitException;
import de.braintags.vertx.util.security.JWTHandler;
import de.braintags.vertx.util.security.JWTSettings;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.ext.jwt.JWT;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
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

  /**
   * The name of the property which is used to store the instance of NetRelay
   */
  public static final String NETRELAY_PROPERTY = "NetRelay";

  // to be able to handle multiple datastores, an IDatastoreCollection will come from pojo-mapper later
  private IDataStore<?, ?> datastore;
  private Settings settings;
  private Router router;
  private MailClient mailClient;
  private JWT jwt;
  /**
   * The mapper factory which translates between the browser and the server
   */
  private NetRelayMapperFactory mapperFactory;
  private NetRelayStoreObjectFactory storeObjectFactory;

  private HttpServer server;

  /*
   * (non-Javadoc)
   *
   * @see io.vertx.core.AbstractVerticle#start()
   */
  @Override
  public void start(final Future<Void> startFuture) {
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
  protected void init(final Handler<AsyncResult<Void>> handler) {
    try {
      router = Router.router(vertx);
      mapperFactory = new NetRelayMapperFactory(this);
      storeObjectFactory = new NetRelayStoreObjectFactory(this);
      initJwt();
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
   *
   */
  private void initJwt() {
    if (settings.getJwtSettings() != null) {
      this.jwt = JWTHandler.createJWT(getVertx(), settings.getJwtSettings());
    }
  }

  /**
   * Set the future to be completed
   *
   * @param startFuture
   */
  protected void initComplete(final Future<Void> startFuture) {
    startFuture.complete();
  }

  private void initMailClient() {
    MailConfig mailConfig = settings.getMailConfig();
    if (mailConfig != null) {
      mailClient = MailClient.createShared(vertx, mailConfig, "netrelay");
      LOGGER.info("MailClient startet with configuration " + mailConfig.toJson());
    } else {
      LOGGER.info("MailClient NOT started, cause not configured");
    }
  }

  /**
   * Retrive the {@link IMapperFactory} which translates between the mappers and the browser
   *
   * @return the {@link IMapperFactory} of NetRelay
   */
  public NetRelayMapperFactory getNetRelayMapperFactory() {
    return mapperFactory;
  }

  /**
   * Get the StoreObjectFactory used by NetRelay
   *
   * @return
   */
  public NetRelayStoreObjectFactory getStoreObjectFactory() {
    return storeObjectFactory;
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
   * Get the actual server port the server is listening on.
   * 
   */
  public int getActualServerPort() {
    if (server == null) {
      throw new IllegalStateException("server not stated");
    }
    return server.actualPort();
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
  protected void initController(final Router router) throws Exception {
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
  public final void initDataStore(final Handler<AsyncResult<Void>> handler)
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

  protected void initHttpServer(final Router router, final Handler<AsyncResult<Void>> handler) {
    HttpServerOptions options = new HttpServerOptions().setPort(settings.getServerPort())
        .setCompressionSupported(settings.isCompressionEnabled());
    server = vertx.createHttpServer(options);
    server.requestHandler(router::accept).listen(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture());
      }
    });
  }

  private void initHttpsServer(final Router router, final Handler<AsyncResult<Void>> handler) {
    if (settings.getSslPort() > 0) {
      LOGGER.info("launching ssl server listening on port " + settings.getSslPort());
      HttpServerOptions options = new HttpServerOptions().setPort(settings.getSslPort());
      options.setCompressionSupported(settings.isCompressionEnabled());
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

  private void handleSslCertificate(final HttpServerOptions options, final Handler<AsyncResult<Void>> handler)
      throws GeneralSecurityException, IOException {
    if (settings.isCertificateSelfSigned()) {
      SelfSignedCertificate certificate = SelfSignedCertificate.create();
      options.setSsl(true).setKeyCertOptions(certificate.keyCertOptions()).setTrustOptions(certificate.trustOptions());
    } else if (settings.getCertificatePath() != null && settings.getCertificatePath().hashCode() != 0) {
      importCertificate(options);
    } else {
      handler.handle(Future.failedFuture(new UnsupportedOperationException(
          "ssl port is set, but no certificate path set and option certificateSelfSigned is not activated")));
    }
  }

  private String validateSslPassword() {
    if (StringUtils.isEmpty(settings.getCertificatePassword())) {
      throw new IllegalArgumentException("The property 'certificatePassword' must be set in the settings of NetRelay");
    }
    return settings.getCertificatePassword();
  }

  private void importCertificate(final HttpServerOptions httpOpts) {
    String certPath = settings.getCertificatePath();

    if (certPath.matches("^.*\\.(pem|PEM)$")) {
      // Use a PEM key/cert pair
      if (settings.getCertificateKeyPath() == null) {
        throw new IllegalArgumentException("The certificateKeyPath is not set for pem certificate");
      }
      httpOpts.setPemKeyCertOptions(
          new PemKeyCertOptions().setCertPath(certPath).setKeyPath(settings.getCertificateKeyPath()));
      httpOpts.setSsl(true);
    } else if (certPath.matches("^.*\\.(P12|p12)$")) {
      String password = validateSslPassword();
      httpOpts.setPfxKeyCertOptions(new PfxOptions().setPath(certPath).setPassword(password));
    } else {
      throw new IllegalArgumentException(
          "Please specify the certificate as PEM file in the format pkcs8, or as p12 file in the Pfx format");
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see io.vertx.core.AbstractVerticle#stop(io.vertx.core.Future)
   */
  @Override
  public void stop(final Future<Void> stopFuture) throws Exception {
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

  protected void addDefaultProcessorDefinitions(final Settings settings) {
    ProcessorDefinition def = new ProcessorDefinition();
    def.setActive(false);
    def.setName("dummyprocessor");
    def.getProcessorProperties().put("demoKey", "demoValue");
    def.setTimeDef("60000");
    settings.getProcessorDefinitons().add(def);
  }

  protected void addDefaultRouterDefinitions(final Settings settings) {
    settings.getRouterDefinitions().add(StaticController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(FavIconController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(CookieController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(SessionController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(TimeoutController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(BodyController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(FailureController.createDefaultRouterDefinition());
  }

  /**
   * Get the {@link IDataStore} for the current instance
   *
   * @return the datastore
   */
  public final IDataStore<?, ?> getDatastore() {
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
   * If the {@link MailConfig} from the {@link Settings} is configured, then this will return
   * the initialized instance of {@link MailClient}
   *
   * @return the mailClient
   */
  public final MailClient getMailClient() {
    return mailClient;
  }

  /**
   * If the {@link JWTSettings} inside the {@link Settings} are configured, this will return a JWT instance to de- and
   * encode JWTs. Otherwise, it will return null
   *
   * @return the jwt
   */
  public JWT getJwt() {
    return jwt;
  }
}

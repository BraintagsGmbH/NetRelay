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

import java.util.List;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.exception.InitException;
import de.braintags.io.vertx.pojomapper.init.IDataStoreInit;
import de.braintags.io.vertx.pojomapper.mongo.init.MongoDataStoreInit;
import de.braintags.netrelay.controller.impl.AuthenticationController;
import de.braintags.netrelay.controller.impl.BodyController;
import de.braintags.netrelay.controller.impl.CookieController;
import de.braintags.netrelay.controller.impl.FailureController;
import de.braintags.netrelay.controller.impl.RedirectController;
import de.braintags.netrelay.controller.impl.SessionController;
import de.braintags.netrelay.controller.impl.StaticController;
import de.braintags.netrelay.controller.impl.ThymeleafTemplateController;
import de.braintags.netrelay.controller.impl.TimeoutController;
import de.braintags.netrelay.controller.impl.persistence.PersistenceController;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.routing.RouterDefinition;
import de.braintags.netrelay.routing.RoutingInit;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public abstract class NetRelay extends AbstractVerticle {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(NetRelay.class);

  // to be able to handle multiple datastores, an IDatastoreCollection will come from pojo-mapper later
  private IDataStore datastore;
  private Settings settings;
  private Router router;

  /**
   * 
   */
  public NetRelay() {
  }

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
          init(startFuture);
        }
      });
    } catch (Exception e) {
      startFuture.fail(e);
    }
  }

  private void init(Future<Void> startFuture) {
    try {
      router = Router.router(vertx);
      initControlller(router);
      initHttpServer(router);
      startFuture.complete();
    } catch (Exception e) {
      startFuture.fail(e);
    }
  }

  /**
   * Get the router, which is used by NetRelay
   * 
   * @return the router
   */
  private Router getRouter() {
    return router;
  }

  /**
   * Init the definitions inside {@link Settings#getRouterDefinitions()}
   * 
   * @throws Exception
   */
  protected void initControlller(Router router) throws Exception {
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
      Settings settings = Settings.loadSettings(this, vertx, context);
      if (!settings.isEdited()) {
        throw new InitException(
            "The settings are not yet edited. Change the value of property 'edited' to true inside the appropriate file");
      }
      return settings;
    } catch (Exception e) {
      LOGGER.error("", e);
      throw e;
    }
  }

  /**
   * Create an instance of {@link IDataStore} which will be used by the current instance of NetRelay.
   * The init is performed by using the {@link Settings#getDatastoreSettings()}
   * 
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

  private void initHttpServer(Router router) {
    HttpServerOptions options = new HttpServerOptions().setPort(settings.getServerPort());
    HttpServer server = vertx.createHttpServer(options);
    server.requestHandler(router::accept).listen();
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.AbstractVerticle#stop(io.vertx.core.Future)
   */
  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    super.stop(stopFuture);
    getDatastore().shutdown(result -> {
      if (result.failed()) {
        stopFuture.fail(result.cause());
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
    Settings settings = new Settings();
    addDefaultRouterDefinitions(settings);
    settings.setDatastoreSettings(MongoDataStoreInit.createDefaultSettings());
    return settings;
  }

  protected void addDefaultRouterDefinitions(Settings settings) {
    settings.getRouterDefinitions().add(CookieController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(SessionController.createDefaultRouterDefinition());
    // settings.getRouterDefinitions().add(UserSessionController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(TimeoutController.createDefaultRouterDefinition());

    settings.getRouterDefinitions().add(RedirectController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(StaticController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(AuthenticationController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(PersistenceController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(BodyController.createDefaultRouterDefinition());
    settings.getRouterDefinitions().add(ThymeleafTemplateController.createDefaultRouterDefinition());

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
    initControlller(router);
  }
}

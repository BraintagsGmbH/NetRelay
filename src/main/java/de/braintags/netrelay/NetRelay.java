/*
 * #%L
 * vertx-pojongo
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

import de.braintags.io.vertx.pojomapper.IDataStore;
import io.vertx.core.AbstractVerticle;
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
  // to be able to handle multiple datastores, an IDatastoreCollection will come from pojo-mapper later
  private IDataStore datastore;
  private Settings settings;

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
  public void start() throws Exception {
    settings = initSettings();
    Router router = initRouter();
    initControlller();
    initHttpServer(router);
  }

  protected void initControlller() {

  }

  /**
   * Initialize the {@link Settings} which are used to init the current instance
   * 
   * @return
   */
  protected Settings initSettings() {
    return Settings.loadSettings(this, vertx, context);
  }

  /**
   * Create an instance of {@link IDataStore} which will be used by the current instance of NetRelay.
   * 
   * @return the {@link IDataStore} to be used
   */
  public abstract IDataStore initDataStore();

  private Router initRouter() {
    return Router.router(vertx);
  }

  private void initHttpServer(Router router) {
    HttpServerOptions options = new HttpServerOptions().setPort(settings.getServerPort());
    HttpServer server = vertx.createHttpServer(options);
    server.requestHandler(router::accept).listen();
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.AbstractVerticle#stop()
   */
  @Override
  public void stop() throws Exception {
    super.stop();
  }

  /**
   * The default instance is requested, when there was no saved instance found
   * 
   * @return
   */
  public Settings createDefaultSettings() {
    Settings settings = new Settings();
    return settings;
  }

}

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
package de.braintags.netrelay.routing;

import de.braintags.netrelay.controller.IController;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * An IRouterDefinition defines in general, which {@link IController} shall be applied for which URI
 * 
 * @author Michael Remme
 * 
 */
public class RouterDefinition {
  private String name;
  private String[] routes;
  private boolean blocking = false;
  private Class<? extends Handler<RoutingContext>> handler;
  private HttpMethod httpMethod;

  /**
   * The name of the definition is used for display
   * 
   * @return the defined name
   */
  public String getName() {
    return name;
  }

  /**
   * The name of the definition is used for display
   * 
   * @param name
   *          the name to set
   */
  public final void setName(String name) {
    this.name = name;
  }

  /**
   * The routes, which are covered by this definition
   * 
   * @return an array of routes, following the syntax defined by {@link io.vertx.ext.web}
   */
  public String[] getRoutes() {
    return routes;
  }

  /**
   * The routes, which are covered by this definition
   * 
   * @param routes
   *          the routes to set
   */
  public final void setRoutes(String[] routes) {
    this.routes = routes;
  }

  /**
   * If this value is true, then the current handler will be added as
   * {@link Route#blockingHandler(io.vertx.core.Handler)}
   * into the {@link Router}
   * 
   * @return true, if definition shall be added as blocking, false otherwise. The default is false
   */
  public boolean isBlocking() {
    return blocking;
  }

  /**
   * If this value is true, then the current handler will be added as
   * {@link Route#blockingHandler(io.vertx.core.Handler)}
   * into the {@link Router}
   * 
   * @param blocking
   *          the blocking to set
   */
  public final void setBlocking(boolean blocking) {
    this.blocking = blocking;
  }

  /**
   * Get the {@link Handler} which shall be executed
   * 
   * @return the handler
   */
  public Class<? extends Handler<RoutingContext>> getHandler() {
    return handler;
  }

  /**
   * Set the {@link Handler} which shall be executed
   * 
   * @param handler
   *          the handler to set
   */
  public final void setHandler(Class<? extends Handler<RoutingContext>> handler) {
    this.handler = handler;
  }

  /**
   * If the current definition shall be bound to an http-method, then it is returned here
   * 
   * @return the {@link HttpMethod} or null, if not bound
   */
  public final HttpMethod getHttpMethod() {
    return httpMethod;
  }

  /**
   * If the current definition shall be bound to an http-method, then it is returned here
   * 
   * @param httpMethod
   *          the httpMethod to set
   */
  public final void setHttpMethod(HttpMethod httpMethod) {
    this.httpMethod = httpMethod;
  }

}

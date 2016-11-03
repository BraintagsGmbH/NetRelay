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
package de.braintags.netrelay.routing;

import java.util.Properties;

import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.controller.IController;
import de.braintags.netrelay.init.Settings;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

/**
 * An IRouterDefinition defines in general, which {@link IController} shall be applied for which URI. A
 * {@link RouterDefinition} is part of the {@link Settings}, by which the current application is initialized
 * 
 * @author Michael Remme
 * 
 */
public class RouterDefinition {
  private String name = null;
  private Class<? extends IController> controller;
  private boolean active = true;
  private HttpMethod httpMethod;
  private String[] routes;
  private boolean blocking = false;
  private boolean failureDefinition = false;
  private Properties handlerProperties = new Properties();
  private CaptureCollection[] captureCollection;

  /**
   * Create an instance of the defined IController and init it with the defined properties
   * 
   * @param vertx
   *          the instance of vertx
   * @param netRelay
   *          the instance of NetRelay
   * @return the initialized {@link IController}
   * @throws Exception
   */
  public IController instantiateController(Vertx vertx, NetRelay netRelay) throws Exception {
    IController ctrl = getController().newInstance();
    ctrl.init(vertx, netRelay, getHandlerProperties(), captureCollection, name);
    ctrl.validateRoutingDefinition(this);
    return ctrl;
  }

  /**
   * Defines properties, by which an {@link IController} is initialized. The properties, which are possible to be
   * defined
   * here, are described inside the appropriate implementation of IController
   * 
   * @return the handlerProperties
   */
  public final Properties getHandlerProperties() {
    return handlerProperties;
  }

  /**
   * Defines properties, by which an {@link IController} is initialized. The properties, which are possible to be
   * defined
   * here, are described inside the appropriate implementation of IController
   * 
   * @param handlerProperties
   *          the handlerProperties to set.
   */
  public final void setHandlerProperties(Properties handlerProperties) {
    this.handlerProperties = handlerProperties;
  }

  /**
   * The name of the definition is used for display and lookup
   * 
   * @return the defined name
   */
  public String getName() {
    return name;
  }

  /**
   * The name of the definition is used for display and lookup
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
    if (routes != null) {
      for (String route : routes) {
        verifyRoute(route);
      }
    }
    this.routes = routes;
  }

  private void verifyRoute(String route) {
    if (route.contains("/:") && route.contains("*")) {
      throw new IllegalArgumentException(
          "Routes with capturing parameters AND asterisk are not working. Use regex or declare multiple routes");
    }
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
   * Get the {@link IController} which shall be executed
   * 
   * @return the controller
   */
  public Class<? extends IController> getController() {
    return controller;
  }

  /**
   * Set the {@link IController} which shall be executed
   * 
   * @param controller
   *          the controller to set
   */
  public final void setController(Class<? extends IController> controller) {
    this.controller = controller;
    if (name == null) {
      name = controller.getSimpleName();
    }
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

  /**
   * Specifies wether the current definition is a definition, which will be used as failure handling for the given route
   * 
   * @return the failureDefinition
   */
  public final boolean isFailureDefinition() {
    return failureDefinition;
  }

  /**
   * Specifies wether the current definition is a definition, which will be used as failure handling for the given route
   * 
   * @param failureDefinition
   *          the failureDefinition to set
   */

  public final void setFailureDefinition(boolean failureDefinition) {
    this.failureDefinition = failureDefinition;
  }

  /**
   * The {@link CaptureCollection} which is defined for the current RouterDefinition
   *
   * @return the captureCollection
   */
  public final CaptureCollection[] getCaptureCollection() {
    return captureCollection;
  }

  /**
   * The {@link CaptureCollection} which is defined for the current RouterDefinition
   *
   * @param captureCollection
   *          the captureCollection to set
   */
  public final void setCaptureCollection(CaptureCollection[] captureCollection) {
    this.captureCollection = captureCollection;
  }

  /**
   * @return the active
   */
  public final boolean isActive() {
    return active;
  }

  /**
   * @param active
   *          the active to set
   */
  public final void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public String toString() {
    return getName();
  }

}

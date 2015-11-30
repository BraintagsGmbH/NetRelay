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
package de.braintags.netrelay.controller.impl;

import java.util.Properties;

import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Used to serve static contens
 * 
 * @author mremme
 * 
 */
public class StaticController extends AbstractController {
  /**
   * The property, by which one can switch on / off the caching of static contents
   */
  public static final String CACHE_ENABLED_PROPERTY = "cacheEnabled";

  /**
   * The property to define the timeout of cached elements, if caching is enabled
   */
  public static final String CACHE_TIMEOUT_PROPERTY = "cacheTimeout";
  private StaticHandler staticHandler;

  /**
   * 
   */
  public StaticController() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.IController#init(io.vertx.core.json.JsonObject)
   */
  @Override
  public void init(Properties properties) {
    staticHandler = StaticHandler.create();
    if (properties.containsKey(CACHE_ENABLED_PROPERTY)) {
      staticHandler.setCachingEnabled(Boolean.valueOf(properties.getProperty(CACHE_ENABLED_PROPERTY, "true")));
    }
    if (properties.containsKey(CACHE_TIMEOUT_PROPERTY)) {
      staticHandler.setCacheEntryTimeout(Integer.parseInt(properties.getProperty(CACHE_TIMEOUT_PROPERTY)));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext event) {
    staticHandler.handle(event);
  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName("StaticController");
    def.setBlocking(false);
    def.setController(StaticController.class);
    def.setHandlerProperties(getDefaultProperties());
    def.setRoutes(new String[] { "/static/*", "/favicon.ico" });
    return def;
  }

  /**
   * Get the default properties for an implementation of StaticController
   * 
   * @return
   */
  public static Properties getDefaultProperties() {
    Properties json = new Properties();
    json.put(CACHE_ENABLED_PROPERTY, "true");
    return json;
  }

}

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
package de.braintags.netrelay.controller;

import java.util.Properties;

import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * A controller to define serving of static contents. Internally a {@link StaticHandler} is used
 * <br>
 * Config-Parameter:<br/>
 * <UL>
 * <LI>{@value #CACHE_ENABLED_PROPERTY}<br/>
 * <LI>{@value #CACHE_TIMEOUT_PROPERTY}<br/>
 * </UL>
 * <br>
 * Request-Parameter:<br/>
 * <br/>
 * Result-Parameter:<br/>
 * <br/>
 *
 * @author Michael Remme
 */
public class StaticController extends AbstractController {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(StaticController.class);

  /**
   * The property, by which one can switch on / off the caching of static contents
   */
  public static final String CACHE_ENABLED_PROPERTY = "cacheEnabled";

  /**
   * The property to define the timeout of cached elements, if caching is enabled
   */
  public static final String CACHE_TIMEOUT_PROPERTY = "cacheTimeout";
  public static final String WEBROOT                = "webroot";
  private StaticHandler staticHandler;

  @Override
  public void initProperties(final Properties properties) {
    if (properties.containsKey(WEBROOT)) {
      staticHandler = StaticHandler.create(properties.getProperty(WEBROOT));
    } else {
      staticHandler = StaticHandler.create();
    }

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
  public void handleController(final RoutingContext event) {
    LOGGER.debug("handling " + getClass().getName() + " for " + event.request().path());
    event.response().headers().set(HttpHeaders.CONTENT_ENCODING, HttpHeaders.IDENTITY);
    staticHandler.handle(event);
  }

  /**
   * Creates a default definition for the current instance
   *
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName(StaticController.class.getSimpleName());
    def.setBlocking(false);
    def.setController(StaticController.class);
    def.setHandlerProperties(getDefaultProperties());
    def.setRoutes(new String[] { "/static/*" });
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

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

import de.braintags.netrelay.RequestUtil;
import de.braintags.netrelay.exception.PropertyRequiredException;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * The RedirectController redirects fitting rules to the page, specified by property
 * {@link RedirectController#DESTINATION_PROPERTY}
 * 
 * @author mremme
 * 
 */
public class RedirectController extends AbstractController {
  /**
   * The propertyname to define the destination, where the current instance is redirecting to
   */
  public static final String DESTINATION_PROPERTY = "destination";

  private String destination;

  /**
   * 
   */
  public RedirectController() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.IController#init(java.util.Properties)
   */
  @Override
  public void init(Properties properties) {
    if (!properties.containsKey(DESTINATION_PROPERTY)) {
      throw new PropertyRequiredException(DESTINATION_PROPERTY);
    }
    destination = properties.getProperty(DESTINATION_PROPERTY);
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext context) {
    HttpServerResponse response = context.response();
    RequestUtil.sendRedirect(response, "/index.html");
  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName("RedirectController");
    def.setBlocking(false);
    def.setController(RedirectController.class);
    def.setHandlerProperties(getDefaultProperties());
    def.setRoutes(new String[] { "/" });
    return def;
  }

  /**
   * Get the default properties for an implementation of StaticController
   * 
   * @return
   */
  public static Properties getDefaultProperties() {
    Properties json = new Properties();
    json.put(DESTINATION_PROPERTY, "/");
    return json;
  }
}

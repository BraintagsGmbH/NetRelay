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
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CookieHandler;

/**
 * The Cookie-Controller uses internally the {@link CookieHandler}, which decodes cookies from the request, makes them
 * available in the {@link RoutingContext} and writes them back in the response.
 * 
 * <br>
 * <br>
 * Config-Parameter:<br/>
 * <br>
 * Request-Parameter:<br/>
 * <br/>
 * Result-Parameter:<br/>
 * <br/>
 * 
 * @author Michael Remme
 */
public class CookieController extends AbstractController {
  private CookieHandler cookieHandler;

  @Override
  public void initProperties(Properties properties) {
    cookieHandler = CookieHandler.create();
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext event) {
    cookieHandler.handle(event);
  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName(CookieController.class.getSimpleName());
    def.setBlocking(false);
    def.setController(CookieController.class);
    def.setHandlerProperties(getDefaultProperties());
    return def;
  }

  /**
   * Get the default properties for an implementation of StaticController
   * 
   * @return
   */
  public static Properties getDefaultProperties() {
    Properties json = new Properties();
    return json;
  }

}

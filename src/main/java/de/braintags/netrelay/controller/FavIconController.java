/*
 * #%L
 * NetRelay
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
import io.vertx.ext.web.handler.FaviconHandler;

/**
 * A controller which is dealing the favicon
 * 
 * Config-Parameter:<br/>
 * possible parameters, which are read from the configuration
 * <UL>
 * <LI>{@value #PATH_PROPERTY} - define the path to the resource; default is "favicon.ico"
 * </UL>
 * <br>
 * 
 * @author Michael Remme
 * 
 */
public class FavIconController extends AbstractController {

  private static final String PATH_PROPERTY = "path";
  private FaviconHandler fh;

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handleController(RoutingContext context) {
    try {
      fh.handle(context);
    } catch (Exception e) {
      context.response().end();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.AbstractController#initProperties(java.util.Properties)
   */
  @Override
  public void initProperties(Properties properties) {
    fh = FaviconHandler.create(properties.getProperty(PATH_PROPERTY, null));
  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName(FavIconController.class.getSimpleName());
    def.setBlocking(false);
    def.setController(FavIconController.class);
    def.setHandlerProperties(getDefaultProperties());
    def.setRoutes(new String[] { "/favicon.ico" });
    return def;
  }

  /**
   * Get the default properties for an implementation of StaticController
   * 
   * @return
   */
  public static Properties getDefaultProperties() {
    Properties json = new Properties();
    json.put(PATH_PROPERTY, "webroot/favicon.ico");
    return json;
  }

}

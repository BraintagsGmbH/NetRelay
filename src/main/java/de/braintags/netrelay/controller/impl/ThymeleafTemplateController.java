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
import java.util.Set;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;

/**
 * This controller is used to process templates based on Thymeleaf
 * 
 * @author mremme
 * 
 */
public class ThymeleafTemplateController extends AbstractController {
  /**
   * The property, by which the mode of Thymeleaf is defined.
   */
  public static final String TEMPLATE_MODE_PROPERTY = "mode";
  /**
   * The property, by which one can switch on / off the caching of templates. Switching off can be useful in development
   * systems to get changes as soon
   */
  public static final String CACHE_ENABLED_PROPERTY = "cacheEnabled";

  private TemplateHandler templateHandler;

  /**
   * 
   */
  public ThymeleafTemplateController() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.IController#init(io.vertx.core.json.JsonObject)
   */
  @Override
  public void init(Properties properties) {
    ThymeleafTemplateEngine thEngine = ThymeleafTemplateEngine.create();
    thEngine.setMode(properties.getProperty(TEMPLATE_MODE_PROPERTY, ThymeleafTemplateEngine.DEFAULT_TEMPLATE_MODE));
    setCachable(thEngine, properties);
    templateHandler = TemplateHandler.create(thEngine);

  }

  private void setCachable(ThymeleafTemplateEngine thEngine, Properties properties) {
    if (properties.containsKey(CACHE_ENABLED_PROPERTY)) {
      boolean cachable = Boolean.valueOf(properties.getProperty(CACHE_ENABLED_PROPERTY));
      TemplateEngine te = thEngine.getThymeleafTemplateEngine();
      Set<ITemplateResolver> trs = te.getTemplateResolvers();
      for (ITemplateResolver tr : trs) {
        ((TemplateResolver) tr).setCacheable(cachable);
      }
    }
  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName("ThymeleafTemplateController");
    def.setBlocking(false);
    def.setController(ThymeleafTemplateController.class);
    def.setHandlerProperties(getDefaultProperties());
    def.setRoutes(new String[] { "/*" });
    return def;
  }

  /**
   * Get the default properties for an implementation of TemplateController
   * 
   * @return
   */
  public static Properties getDefaultProperties() {
    Properties json = new Properties();
    json.put(TEMPLATE_MODE_PROPERTY, ThymeleafTemplateEngine.DEFAULT_TEMPLATE_MODE);
    json.put(CACHE_ENABLED_PROPERTY, "true");
    return json;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext event) {
    templateHandler.handle(event);
  }

}

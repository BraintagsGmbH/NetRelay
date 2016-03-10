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
import org.thymeleaf.extras.conditionalcomments.dialect.ConditionalCommentsDialect;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;

/**
 * This controller is used to process templates based on the template engine Thymeleaf
 * <br>
 * <br>
 * Config-Parameter:<br/>
 * <UL>
 * <LI>{@value #TEMPLATE_MODE_PROPERTY}<br/>
 * <LI>{@value #TEMPLATE_DIRECTORY_PROPERTY}<br/>
 * <LI>{@value #CONTENT_TYPE_PROPERTY}<br/>
 * <LI>{@value #CACHE_ENABLED_PROPERTY}<br/>
 * </UL>
 * <br>
 * Request-Parameter:<br/>
 * <br/>
 * Result-Parameter:<br/>
 * <br/>
 * 
 * @author Michael Remme
 */
public class ThymeleafTemplateController extends AbstractController {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(ThymeleafTemplateController.class);

  /**
   * The property, by which the mode of Thymeleaf is defined. By default this is set to "XHTML"
   */
  public static final String TEMPLATE_MODE_PROPERTY = "mode";

  /**
   * The property, by which the directory is defined, where templates are residing
   */
  public static final String TEMPLATE_DIRECTORY_PROPERTY = "templateDirectory";

  /**
   * The default directory for templates
   */
  public static final String DEFAULT_TEMPLATE_DIRECTORY = TemplateHandler.DEFAULT_TEMPLATE_DIRECTORY;

  /**
   * The property, which defines the content type to be handled. PEr default this is text/html
   */
  public static final String CONTENT_TYPE_PROPERTY = "contentType";

  /**
   * The default content type to be managed
   */
  public static final String DEFAULT_CONTENT_TYPE = TemplateHandler.DEFAULT_CONTENT_TYPE;

  /**
   * The property, by which one can switch on / off the caching of templates. Switching off can be useful in development
   * systems to get changes as soon
   */
  public static final String CACHE_ENABLED_PROPERTY = "cacheEnabled";

  private TemplateHandler templateHandler;

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext event) {
    String path = event.request().path();
    LOGGER.info("handling template for url " + event.normalisedPath() + " | " + path);
    if (path.endsWith("/")) {
      LOGGER.info("REROUTING TO: " + path);
      path += "index.html";
      event.reroute(path);
    } else {
      templateHandler.handle(event);
    }
  }

  @Override
  public void initProperties(Properties properties) {
    ThymeleafTemplateEngine thEngine = createTemplateEngine(properties);
    templateHandler = TemplateHandler.create(thEngine, getTemplateDirectory(properties), getContentType(properties));
  }

  /**
   * Creates a ThymeleafEngine by using the defined properties
   * 
   * @param properties
   * @return
   */
  public static ThymeleafTemplateEngine createTemplateEngine(Properties properties) {
    ThymeleafTemplateEngine thEngine = ThymeleafTemplateEngine.create();
    thEngine.setMode(properties.getProperty(TEMPLATE_MODE_PROPERTY, ThymeleafTemplateEngine.DEFAULT_TEMPLATE_MODE));
    setCachable(thEngine, properties);
    return thEngine;
  }

  /**
   * Get the info about the defined template directory in the properties
   * 
   * @param props
   *          the configuration
   * @return the defined value inside the properties or {@value #DEFAULT_TEMPLATE_DIRECTORY}
   */
  public static String getTemplateDirectory(Properties props) {
    return (String) props.getOrDefault(TEMPLATE_DIRECTORY_PROPERTY, DEFAULT_TEMPLATE_DIRECTORY);
  }

  private String getContentType(Properties props) {
    return (String) props.getOrDefault(CONTENT_TYPE_PROPERTY, DEFAULT_CONTENT_TYPE);
  }

  private static void setCachable(ThymeleafTemplateEngine thEngine, Properties properties) {
    if (properties.containsKey(CACHE_ENABLED_PROPERTY)) {
      boolean cachable = Boolean.parseBoolean(properties.getProperty(CACHE_ENABLED_PROPERTY));
      TemplateEngine te = thEngine.getThymeleafTemplateEngine();
      ConditionalCommentsDialect ccd = new ConditionalCommentsDialect();
      te.addDialect(ccd);
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
    def.setName(ThymeleafTemplateController.class.getSimpleName());
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
    json.put(CONTENT_TYPE_PROPERTY, DEFAULT_CONTENT_TYPE);
    json.put(TEMPLATE_DIRECTORY_PROPERTY, DEFAULT_TEMPLATE_DIRECTORY);
    return json;
  }

}

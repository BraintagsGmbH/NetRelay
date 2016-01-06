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
package de.braintags.netrelay.controller.impl;

import java.util.Properties;

import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.TimeoutHandler;

/**
 * This controller defines for the specified routes, after which time the request processing is stopped. It uses
 * internally a {@link TimeoutHandler}
 * <br>
 * <br>
 * Config-Parameter:<br/>
 * <UL>
 * <LI>{@value #TIMEOUT_PROP}<br/>
 * </UL>
 * <br>
 * Request-Parameter:<br/>
 * <br/>
 * Result-Parameter:<br/>
 * <br/>
 * 
 * @author Michael Remme
 */
public class TimeoutController extends AbstractController {
  private TimeoutHandler timeoutHandler;

  /**
   * The property, by which the timeout in milliseconds is defined
   */
  public static final String TIMEOUT_PROP = "timeout";
  public static final long DEFAULT_TIMEOUT = 30000;

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext event) {
    timeoutHandler.handle(event);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractController#initProperties(java.util.Properties)
   */
  @Override
  public void initProperties(Properties properties) {
    long timeout = Long.parseLong((String) properties.getOrDefault(TIMEOUT_PROP, String.valueOf(DEFAULT_TIMEOUT)));
    timeoutHandler = TimeoutHandler.create(timeout);
  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName(TimeoutController.class.getSimpleName());
    def.setBlocking(false);
    def.setController(TimeoutController.class);
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
    json.put(TIMEOUT_PROP, String.valueOf(DEFAULT_TIMEOUT));
    return json;
  }

}

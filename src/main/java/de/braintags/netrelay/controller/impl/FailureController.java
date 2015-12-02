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

import de.braintags.io.vertx.util.ExceptionUtil;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.ext.web.RoutingContext;

/**
 * A Controller for failing calls
 * 
 * @author mremme
 * 
 */
public class FailureController extends AbstractController {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(FailureController.class);

  /**
   * 
   */
  public FailureController() {
  }

  @Override
  public void initProperties(Properties properties) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext event) {
    String reply = "Status-Code: " + event.statusCode();
    LOGGER.info(reply);
    if (event.failure() != null) {
      reply += "\n" + event.failure().toString();
      reply += "\n" + ExceptionUtil.getStackTrace(event.failure());
      LOGGER.error("", event.failure());
    }
    if (!event.response().ended())
      event.response().end(reply);

  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName("FailureController");
    def.setBlocking(false);
    def.setFailureDefinition(true);
    def.setController(FailureController.class);
    return def;
  }

}

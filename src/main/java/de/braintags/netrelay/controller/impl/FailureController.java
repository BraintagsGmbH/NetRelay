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
 * A Controller for failing calls, which is sending an error code. When this controller is added to a RouterDefinition,
 * the {@link RouterDefinition#setFailureDefinition(boolean)} should be set to true
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
public class FailureController extends AbstractController {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(FailureController.class);

  @Override
  public void initProperties(Properties properties) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext context) {
    String reply = String.format("Statuscode %d for request %s", context.statusCode(), context.request().path());
    LOGGER.info(reply);
    if (context.failure() != null) {
      reactByException(context);
      ;
    } else {
      reactByStatusCode(context);
    }

  }

  private void reactByException(RoutingContext context) {
    Throwable error = context.failure();
    String reply = String.format("Statuscode %d for request %s", context.statusCode(), context.request().path());
    reply += "\n" + error.toString();
    reply += "\n" + ExceptionUtil.getStackTrace(error);
    LOGGER.error("", error);
    if (!context.response().ended()) {
      context.response().end(reply);
    }
  }

  private void reactByStatusCode(RoutingContext context) {
    switch (context.statusCode()) {
    default:
      handleDefaultStatus(context);

    }
  }

  private void handleDefaultStatus(RoutingContext context) {
    if (!context.response().ended()) {
      context.response().setStatusCode(context.statusCode());
      context.response().end();
    }
  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName(FailureController.class.getSimpleName());
    def.setBlocking(false);
    def.setFailureDefinition(true);
    def.setController(FailureController.class);
    return def;
  }

}

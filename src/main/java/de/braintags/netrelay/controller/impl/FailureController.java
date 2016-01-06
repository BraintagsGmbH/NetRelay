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
  public void handle(RoutingContext event) {
    String reply = String.format("Statuscode %d for request %s", event.statusCode(), event.request().path());
    LOGGER.info(reply);
    if (event.failure() != null) {
      reply += "\n" + event.failure().toString();
      reply += "\n" + ExceptionUtil.getStackTrace(event.failure());
      LOGGER.error("", event.failure());
    }
    if (!event.response().ended()) {
      event.response().setStatusCode(event.statusCode());
      event.response().end();
      // event.response().end(reply);
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

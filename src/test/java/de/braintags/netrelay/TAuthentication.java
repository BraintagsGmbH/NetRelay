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
package de.braintags.netrelay;

import org.junit.Test;

import de.braintags.netrelay.controller.impl.authentication.AuthenticationController;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TAuthentication extends NetRelayBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TAuthentication.class);

  @Test
  public void testDisplaySingleRecord(TestContext context) {
    try {
      String url = "/private/privatePage.html";
      testRequest(context, HttpMethod.POST, url, null, resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        context.assertTrue(resp.content.toString().contains("testmapper for display"), "Expected name not found");
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }

  }

  // test double logout, or logout when no user is logged in

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelayBaseTest#modifySettings(de.braintags.netrelay.init.Settings)
   */
  @Override
  protected void modifySettings(TestContext context, Settings settings) {
    super.modifySettings(context, settings);
    RouterDefinition def = settings.getRouterDefinitions()
        .getNamedDefinition(AuthenticationController.class.getSimpleName());
    def.setRoutes(new String[] { "/privat/*" });

  }

}

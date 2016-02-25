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

import de.braintags.netrelay.controller.impl.FailureController;
import de.braintags.netrelay.controller.impl.ThymeleafTemplateController;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;

/**
 * Test the FAilureController
 * 
 * @author Michael Remme
 * 
 */
public class TFailureController extends NetRelayBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TFailureController.class);

  @Test
  public void testErrorCode(TestContext context) throws Exception {
    try {
      resetRoutes();
      String url = "/isNixDa";
      testRequest(context, HttpMethod.GET, url, req -> {

      } , resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        LOGGER.info("HEADERS: " + resp.headers);
        context.assertNotNull(resp.headers.get("location"), "no redirect executed");
        context.assertTrue(resp.headers.get("location").contains("/error/NotFound.html"),
            "redirect expected to errorpage");
      } , 302, "Found", null);

    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * @throws Exception
   */
  private void resetRoutes() throws Exception {
    RouterDefinition def = netRelay.getSettings().getRouterDefinitions()
        .getNamedDefinition(FailureController.class.getSimpleName());
    def.setFailureDefinition(true);
    def.getHandlerProperties().put(FailureController.ERRORCODE_START_PARAMETER + "400", "/error/NotFound.html");
    def.getHandlerProperties().put(FailureController.EXCEPTION_START_PARAMETER + "java.lang.Exception",
        "/error/exception.html");
    def.getHandlerProperties().put(FailureController.DEFAULT_PROPERTY, "/error/defaultRedirect.html");
    def.setRoutes(new String[] { "/*" });

    def = netRelay.getSettings().getRouterDefinitions()
        .getNamedDefinition(ThymeleafTemplateController.class.getSimpleName());
    def.setRoutes(new String[] { "/*" });

    netRelay.resetRoutes();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelayBaseTest#initTest()
   */
  @Override
  public void initTest(TestContext context) {
    super.initTest(context);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelayBaseTest#modifySettings(de.braintags.netrelay.init.Settings)
   */
  @Override
  protected void modifySettings(TestContext context, Settings settings) {
    super.modifySettings(context, settings);
  }

}

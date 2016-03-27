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
package de.braintags.netrelay.unit;

import org.junit.Test;

import de.braintags.netrelay.controller.CreateErrorController;
import de.braintags.netrelay.controller.FailureController;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;

/**
 * Test the FailureController
 * 
 * @author Michael Remme
 * 
 */
public class TFailureController extends NetRelayBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TFailureController.class);

  @Test
  public void testException(TestContext context) throws Exception {
    try {
      resetRoutes(RuntimeException.class, -1);
      String url = "/isNixDa";
      testRequest(context, HttpMethod.GET, url, req -> {

      }, resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        LOGGER.info("HEADERS: " + resp.headers);
        context.assertNotNull(resp.headers.get("location"), "no redirect executed");
        context.assertTrue(resp.headers.get("location").contains("/error/exception.html"),
            "redirect expected to errorpage");
      }, 302, "Found", null);

    } catch (Exception e) {
      context.fail(e);
    }
  }

  @Test
  public void testErrorCode(TestContext context) throws Exception {
    try {
      resetRoutes(null, 400);
      String url = "/isNixDa";
      testRequest(context, HttpMethod.GET, url, req -> {

      }, resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        LOGGER.info("HEADERS: " + resp.headers);
        context.assertNotNull(resp.headers.get("location"), "no redirect executed");
        context.assertTrue(resp.headers.get("location").contains("/error/NotFound.html"),
            "redirect expected to errorpage");
      }, 302, "Found", null);

    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * @throws Exception
   */
  private void resetRoutes(Class<? extends Exception> exception, int errorCode) throws Exception {
    RouterDefinition rdf = netRelay.getSettings().getRouterDefinitions()
        .getNamedDefinition(FailureController.class.getSimpleName());
    rdf.setFailureDefinition(true);
    rdf.getHandlerProperties().put(FailureController.ERRORCODE_START_PARAMETER + "400", "/error/NotFound.html");
    rdf.getHandlerProperties().put(FailureController.EXCEPTION_START_PARAMETER + "java.lang.Exception",
        "/error/exception.html");
    rdf.getHandlerProperties().put(FailureController.DEFAULT_PROPERTY, "/error/defaultRedirect.html");
    rdf.setRoutes(new String[] { "/*" });

    RouterDefinition def = new RouterDefinition();
    def.setController(CreateErrorController.class);
    CreateErrorController.errorCode = errorCode;
    CreateErrorController.exception = exception;
    netRelay.getSettings().getRouterDefinitions().addBefore(FailureController.class.getSimpleName(), def);

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

}

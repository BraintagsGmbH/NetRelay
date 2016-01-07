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
import de.braintags.netrelay.model.Member;
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

  /**
   * Send a direct, successful login and - cause no parameter
   * {@link AuthenticationController#DIRECT_LOGGED_IN_OK_URL_PROP} is set -
   * check the page called
   * 
   * @param context
   */
  @Test
  public void testDirectLoginWithoutDestination(TestContext context) {
    Member member = new Member();
    member.setUserName("testuser");
    member.setPassword("testpassword");
    member = createOrFindMember(context, netRelay.getDatastore(), member);
    context.assertNotNull(member, "Member must not be null");
go on here to call login 
    try {
      resetRoutes();
      String url = "/private/privatePage.html";
      testRequest(context, HttpMethod.POST, url, null, resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        LOGGER.info("HEADERS: " + resp.headers);
        context.assertTrue(resp.headers.contains("location"), "parameter location does not exist");
        context.assertTrue(resp.headers.get("location").equals("/member/login"), "Expected location /member/login");
      } , 302, "Found", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * Improves that for a call to a protected page a redirect is sent
   * 
   * @param context
   */
  @Test
  public void testSimpleLogin(TestContext context) {
    try {
      resetRoutes();
      String url = "/private/privatePage.html";
      testRequest(context, HttpMethod.POST, url, null, resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        LOGGER.info("HEADERS: " + resp.headers);
        context.assertTrue(resp.headers.contains("location"), "parameter location does not exist");
        context.assertTrue(resp.headers.get("location").equals("/member/login"), "Expected location /member/login");
      } , 302, "Found", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  // test wrong login

  // test double logout, or logout when no user is logged in

  /**
   * @throws Exception
   */
  private void resetRoutes() throws Exception {
    RouterDefinition def = netRelay.getSettings().getRouterDefinitions()
        .getNamedDefinition(AuthenticationController.class.getSimpleName());
    def.setRoutes(new String[] { "/private/*" });
    netRelay.resetRoutes();
  }

}

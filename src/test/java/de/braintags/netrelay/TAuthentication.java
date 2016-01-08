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
import de.braintags.netrelay.util.MultipartUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TAuthentication extends NetRelayBaseTest {
  /**
   * Comment for <code>PROTECTED_URL</code>
   */
  private static final String PROTECTED_URL = "/private/privatePage.html";
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TAuthentication.class);

  /**
   * Send a direct, successful login and - cause no parameter
   * {@link AuthenticationController#DIRECT_LOGGED_IN_OK_URL_PROP} is set -
   * check, that the standard reply is sent
   * 
   * @param context
   */
  @Test
  public void testDirectLoginWithoutDestination(TestContext context) {
    Member member = createMember(context);
    try {
      resetRoutes(null);
      MultipartUtil mu = new MultipartUtil();
      mu.addFormField("username", member.getUserName());
      mu.addFormField("password", member.getPassword());

      String url = AuthenticationController.DEFAULT_LOGIN_ACTION_URL;
      testRequest(context, HttpMethod.POST, url, req -> {
        mu.finish(req);
      } , resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        LOGGER.info("HEADERS: " + resp.headers);
        context.assertTrue(resp.content.contains("Login successful"), "required text in reply not found");
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * Send a direct, successful login and check that the page is called, which was set by
   * {@link AuthenticationController#DIRECT_LOGGED_IN_OK_URL_PROP}
   * 
   * @param context
   */
  @Test
  public void testDirectLoginWitDestination(TestContext context) {
    Member member = createMember(context);
    try {
      resetRoutes("/loginSuccess.html");
      MultipartUtil mu = new MultipartUtil();
      mu.addFormField("username", member.getUserName());
      mu.addFormField("password", member.getPassword());

      String url = AuthenticationController.DEFAULT_LOGIN_ACTION_URL;
      testRequest(context, HttpMethod.POST, url, req -> {
        mu.finish(req);
      } , resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        LOGGER.info("HEADERS: " + resp.headers);
        improveRedirect("/loginSuccess.html", context, resp);
      } , 302, "Found", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * Send a direct, successful login and - cause no parameter
   * {@link AuthenticationController#DIRECT_LOGGED_IN_OK_URL_PROP} is set -
   * check, that the standard reply is sent
   * 
   * @param context
   */
  @Test
  public void testDirectLoginWithoutDestination_WrongLogin(TestContext context) {
    Member member = createMember(context);
    try {
      resetRoutes(null);
      MultipartUtil mu = new MultipartUtil();
      mu.addFormField("username", "wrongUsername");
      mu.addFormField("password", member.getPassword());

      String url = AuthenticationController.DEFAULT_LOGIN_ACTION_URL;
      testRequest(context, HttpMethod.POST, url, req -> {

        mu.finish(req);

      } , resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        LOGGER.info("HEADERS: " + resp.headers);
      } , 403, "Forbidden", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * Perform login and logout by resusing sent cookie
   */
  @Test
  public void loginLogout(TestContext context) {
    Member member = createMember(context);
    Buffer cookie = Buffer.buffer();
    try {
      resetRoutes(null);
      MultipartUtil mu = new MultipartUtil();
      mu.addFormField("username", member.getUserName());
      mu.addFormField("password", member.getPassword());

      // first perform the login and remember cookie
      String url = AuthenticationController.DEFAULT_LOGIN_ACTION_URL;
      testRequest(context, HttpMethod.POST, url, req -> {
        mu.finish(req);
      } , resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        LOGGER.info("HEADERS: " + resp.headers);
        context.assertTrue(resp.content.contains("Login successful"), "required text in reply not found");
        String setCookie = resp.headers.get("Set-Cookie");
        context.assertNotNull(setCookie, "Cookie not found");
        cookie.appendString(setCookie);
      } , 200, "OK", null);

      // second call protected page and set cookie
      url = PROTECTED_URL;
      testRequest(context, HttpMethod.POST, url, httpConn -> {
        httpConn.headers().set("Cookie", cookie.toString());
      } , resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        LOGGER.info("HEADERS: " + resp.headers);
        context.assertTrue(resp.content.contains("PRIVAT"), "protected page should be read, but was not");
        String setCookie = resp.headers.get("Set-Cookie");
        context.assertNull(setCookie, "Cookie should not be sent here");
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * Perform login and logout
   */
  @Test
  public void logoutWithoutLogin(TestContext context) {
    try {
      resetRoutes(null);
      String url = AuthenticationController.DEFAULT_LOGOUT_ACTION_URL;
      testRequest(context, HttpMethod.POST, url, null, resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        LOGGER.info("HEADERS: " + resp.headers);
        improveRedirect(AuthenticationController.DEFAULT_LOGOUT_DESTINATION, context, resp);
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
      resetRoutes(null);
      String url = PROTECTED_URL;
      testRequest(context, HttpMethod.POST, url, null, resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        LOGGER.info("HEADERS: " + resp.headers);
        improveRedirect(AuthenticationController.DEFAULT_LOGIN_ACTION_URL, context, resp);
      } , 302, "Found", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * "loginPage" : "/backend/login.html",
   * "logoutAction" : "/member/logout",
   * "logoutDestinationPage": "/backend/login.html",
   * "roleField" : "roles",
   * "collectionName" : "Member",
   * "loginAction" : "/member/login",
   * "passwordField" : "password",
   * "usernameField" : "userName",
   * "authProvider" : "MongoAuth"
   * 
   * @throws Exception
   */
  private void resetRoutes(String directLoginPage) throws Exception {
    RouterDefinition def = netRelay.getSettings().getRouterDefinitions()
        .getNamedDefinition(AuthenticationController.class.getSimpleName());
    def.setRoutes(new String[] { "/private/*" });
    def.getHandlerProperties().put("collectionName", "Member");
    def.getHandlerProperties().put("passwordField", "password");
    def.getHandlerProperties().put("usernameField", "userName");
    def.getHandlerProperties().put("roleField", "roles");
    if (directLoginPage != null) {
      def.getHandlerProperties().put(AuthenticationController.DIRECT_LOGGED_IN_OK_URL_PROP, directLoginPage);
    }
    netRelay.resetRoutes();
  }

  /**
   * @param context
   * @return
   */
  private Member createMember(TestContext context) {
    Member member = new Member();
    member.setUserName("testuser");
    member.setPassword("testpassword");
    member = createOrFindMember(context, netRelay.getDatastore(), member);
    context.assertNotNull(member, "Member must not be null");
    return member;
  }

  private void improveRedirect(String redirectPath, TestContext context, ResponseCopy resp) {
    context.assertTrue(resp.headers.contains("location"), "parameter location does not exist");
    context.assertTrue(resp.headers.get("location").startsWith(redirectPath), "Expected redirect to " + redirectPath);
  }

}

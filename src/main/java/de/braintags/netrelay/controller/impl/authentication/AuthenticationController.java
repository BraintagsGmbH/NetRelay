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
package de.braintags.netrelay.controller.impl.authentication;

import java.util.Properties;

import de.braintags.netrelay.RequestUtil;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.mongo.MongoAuth;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.BasicAuthHandler;
import io.vertx.ext.web.handler.FormLoginHandler;
import io.vertx.ext.web.handler.RedirectAuthHandler;
import io.vertx.ext.web.handler.UserSessionHandler;

/**
 * Controller performs the authentication, login and logout of members.
 * If a call to a protected page is performed, a 302-redirect to the defined login page is processed
 * If the login failed, then the controller tries to reroute the call to the defined login page again. Before it is
 * setting the parameter {@value #AUTHENTICATION_ERROR_PARAM}. If this page is not defined, then a 403 error is sent.
 * 
 * Logout: performs logout and stores message in context with key LOGOUT_MESSAGE_PROP
 * 
 * 
 * Config-Parameter:<br/>
 * <UL>
 * <LI>{@value #LOGIN_ACTION_URL_PROP}
 * <LI>{@value #LOGOUT_ACTION_URL_PROP}
 * <LI>{@value #LOGOUT_DESTINATION_PAGE_PROP}
 * <LI>{@value #DIRECT_LOGGED_IN_OK_URL_PROP}
 * <LI>{@value #LOGOUT_MESSAGE_PROP}
 * <LI>{@value #LOGIN_PAGE_PROP}
 * <LI>{@value #AUTH_HANDLER_PROP} - the name of the property, which defines the {@link AuthHandler} to be used.
 * Possible values are:
 * {@link AuthHandlerEnum#BASIC}, {@link AuthHandlerEnum#REDIRECT}
 * <LI>additionally add the properties of {@link AbstractAuthProviderController}
 * </UL>
 * <br>
 * Request-Parameter:<br/>
 * <br/>
 * Result-Parameter:<br/>
 * {@value #AUTHENTICATION_ERROR_PARAM} the parameter, where an error String of a failed authentication is stored in
 * the context
 * <br/>
 * 
 * 
 * @author mremme
 * 
 */
public class AuthenticationController extends AbstractAuthProviderController {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AuthenticationController.class);

  /**
   * If an error occurs during login, then the error is added into the context. With this parameter you can define the
   * name of the parameter, by which it is added into the context.
   */
  public static final String AUTHENTICATION_ERROR_PARAM = "authenticationError";

  /**
   * With this property the url for the login can be defined. It is the url, which is called by a login form and is a
   * virtual url, where the {@link FormLoginHandler} is processed. Default is "/member/login"
   */
  public static final String LOGIN_ACTION_URL_PROP = "loginAction";

  /**
   * The url where a logout action is performed, if a member is logged in
   */
  public static final String LOGOUT_ACTION_URL_PROP = "logoutAction";

  /**
   * By this property you can define the destination page, which is called after a logout
   */
  public static final String LOGOUT_DESTINATION_PAGE_PROP = "logoutDestinationPage";

  /**
   * This is the name of the parameter, which defines the url to redirect to, if the user logs in directly at the url of
   * the form login handler without being redirected here first. ( parameter used by FormLoginHandler )
   */
  public static final String DIRECT_LOGGED_IN_OK_URL_PROP = "directLoggedInOKURL";

  /**
   * The default url, where the login action is performed
   */
  public static final String DEFAULT_LOGIN_ACTION_URL = "/member/login";

  /**
   * The default url, where the login action is performed
   */
  public static final String DEFAULT_LOGOUT_ACTION_URL = "/member/logout";

  /**
   * The default url which is called after a logout
   */
  public static final String DEFAULT_LOGOUT_DESTINATION = "/index.html";

  /**
   * This property name is used as key, to store a logout message into the context
   */
  public static final String LOGOUT_MESSAGE_PROP = "logoutMessage";

  /**
   * Defines the name of the {@link AuthHandler} to be used
   */
  public static final String AUTH_HANDLER_PROP = "authHandler";

  /**
   * The name of the property which defines the login page to be used
   */
  public static final String LOGIN_PAGE_PROP = "loginPage";

  protected AuthHandler authHandler;
  private String loginPage;

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext event) {
    authHandler.handle(event);

  }

  @Override
  public void initProperties(Properties properties) {
    super.initProperties(properties);
    loginPage = (String) properties.get(LOGIN_PAGE_PROP);
    setupAuthentication(properties, getAuthProvider());
    initUserSessionHandler();
    initLoginAction();
    initLogoutAction();
    initLogger();
  }

  private void initLogger() {
    getNetRelay().getRouter().route().handler(context -> {
      LOGGER.info("USER: " + context.user());
      context.next();
    });
  }

  private void initUserSessionHandler() {
    getNetRelay().getRouter().route().handler(UserSessionHandler.create(getAuthProvider()));
  }

  private void initLogoutAction() {
    String logoutUrl = readProperty(LOGOUT_ACTION_URL_PROP, DEFAULT_LOGOUT_ACTION_URL, false);
    String logoutDestinationURL = readProperty(LOGOUT_DESTINATION_PAGE_PROP, DEFAULT_LOGOUT_DESTINATION, false);
    getNetRelay().getRouter().route(logoutUrl).handler(context -> {
      if (context.user() != null) {
        context.clearUser();
        RequestUtil.removeCurrentUser(context);
      }
      String path = logoutDestinationURL + "?" + LOGOUT_MESSAGE_PROP + "=success";
      RequestUtil.sendRedirect(context.response(), path);
    });

  }

  private void initLoginAction() {
    String loginUrl = readProperty(LOGIN_ACTION_URL_PROP, DEFAULT_LOGIN_ACTION_URL, false);
    String directLoginUrl = readProperty(DIRECT_LOGGED_IN_OK_URL_PROP, null, false);
    String authErrorParam = readProperty(AUTHENTICATION_ERROR_PARAM, null, false);

    FormLoginHandlerBt fl = new FormLoginHandlerBt(getAuthProvider());
    if (directLoginUrl != null) {
      fl.setDirectLoggedInOKURL(directLoginUrl);
    }
    if (loginPage != null) {
      fl.setLoginPage(loginPage);
    }
    if (authErrorParam != null) {
      fl.setAuthenticationErrorParameter(authErrorParam);
    }
    getNetRelay().getRouter().route(loginUrl).handler(fl);
  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName(AuthenticationController.class.getSimpleName());
    def.setBlocking(false);
    def.setController(AuthenticationController.class);
    def.setHandlerProperties(getDefaultProperties());
    def.setRoutes(new String[] { "/member/*" });
    return def;
  }

  /**
   * Get the default properties for an implementation of StaticController
   * 
   * @return
   */
  public static Properties getDefaultProperties() {
    Properties json = new Properties();
    json.put(LOGIN_PAGE_PROP, "/member/login");
    json.put(AUTH_PROVIDER_PROP, AUTH_PROVIDER_MONGO);
    json.put(MongoAuth.PROPERTY_PASSWORD_FIELD, "password");
    json.put(MongoAuth.PROPERTY_USERNAME_FIELD, "username");
    json.put(MongoAuth.PROPERTY_COLLECTION_NAME, "usertable");
    json.put(MongoAuth.PROPERTY_ROLE_FIELD, "roles");
    json.put(LOGIN_ACTION_URL_PROP, DEFAULT_LOGIN_ACTION_URL);
    json.put(LOGOUT_ACTION_URL_PROP, DEFAULT_LOGOUT_ACTION_URL);
    json.put(LOGOUT_DESTINATION_PAGE_PROP, DEFAULT_LOGOUT_DESTINATION);
    return json;
  }

  private void setupAuthentication(Properties properties, AuthProvider authProvider) {
    AuthHandlerEnum ae = AuthHandlerEnum.valueOf(readProperty(AUTH_HANDLER_PROP, "REDIRECT", false));
    switch (ae) {
    case BASIC:
      authHandler = BasicAuthHandler.create(authProvider);
      break;

    case REDIRECT:
      authHandler = new RedirectAuthHandlerBt(authProvider, loginPage, RedirectAuthHandler.DEFAULT_RETURN_URL_PARAM);
      break;

    default:
      throw new UnsupportedOperationException("unsupported definition for authentication handler: " + ae);
    }
  }

  public enum AuthHandlerEnum {
    /**
     * Used as possible value for {@link AbstractAuthController#AUTH_HANDLER_PROP} and creates a
     * {@link BasicAuthHandler}
     */
    BASIC(),
    /**
     * Used as possible value for {@link AbstractAuthController#AUTH_HANDLER_PROP} and creates a
     * {@link RedirectAuthHandler}
     */
    REDIRECT();
  }
}

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

import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.ext.auth.mongo.MongoAuth;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.FormLoginHandler;

/**
 * Controller performs the authentication of members
 * 
 * @author mremme
 * 
 */
public class AuthenticationController extends AbstractAuthController {
  /**
   * With this property the url for the login can be defined. It is the url, which is called by a login form and is a
   * virtual url, where the {@link FormLoginHandler} is processed. Default is "/member/login"
   */
  public static final String LOGIN_ACTION_URL_PROP = "loginAction";

  /**
   * The url where a logout is performed, if a member is logged in
   */
  public static final String LOGOUT_URL_PROP = "logoutUrl";

  /**
   * By this property you can define the destination page, which is called after a logout
   */
  public static final String LOGOUT_DESTINATION_PAGE_PROP = "logoutDestinationPage";

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
    initLoginAction();
    initLogoutAction();
  }

  private void initLogoutAction() {
    String logoutUrl = readProperty(LOGOUT_URL_PROP, DEFAULT_LOGOUT_ACTION_URL, false);
    String logoutDestinationURL = readProperty(LOGOUT_DESTINATION_PAGE_PROP, DEFAULT_LOGOUT_DESTINATION, false);
    getNetRelay().getRouter().route(logoutUrl).handler(context -> {
      if (context.user() != null) {
        context.clearUser();
        context.reroute(logoutDestinationURL);
      }
    });

  }

  private void initLoginAction() {
    String loginUrl = readProperty(LOGIN_ACTION_URL_PROP, DEFAULT_LOGIN_ACTION_URL, false);
    getNetRelay().getRouter().route(loginUrl).handler(FormLoginHandler.create(authProvider));
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
    json.put(LOGOUT_URL_PROP, DEFAULT_LOGOUT_ACTION_URL);
    return json;
  }

}

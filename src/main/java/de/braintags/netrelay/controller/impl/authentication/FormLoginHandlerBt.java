/*
 * #%L
 * vertx-pojongo
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

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.FormLoginHandler;

/**
 * An extension of FormLoginHandlerImpl cause to the handling of failed logins
 * 
 * @author Michael Remme
 * 
 */
public class FormLoginHandlerBt implements FormLoginHandler {

  private static final Logger log = LoggerFactory.getLogger(FormLoginHandlerBt.class);

  private final AuthProvider authProvider;

  private String usernameParam;
  private String passwordParam;
  private String returnURLParam;
  private String directLoggedInOKURL;
  private String defaultAuthErrorPage = null;

  public FormLoginHandlerBt(AuthProvider authProvider) {
    this(authProvider, DEFAULT_USERNAME_PARAM, DEFAULT_PASSWORD_PARAM, DEFAULT_RETURN_URL_PARAM, null, null);
  }

  public FormLoginHandlerBt(AuthProvider authProvider, String usernameParam, String passwordParam,
      String returnURLParam, String directLoggedInOKURL, String defaultAuthErrorPage) {
    this.authProvider = authProvider;
    this.usernameParam = usernameParam;
    this.passwordParam = passwordParam;
    this.returnURLParam = returnURLParam;
    this.directLoggedInOKURL = directLoggedInOKURL;
    this.defaultAuthErrorPage = defaultAuthErrorPage;
  }

  /**
   * If the originally called page can not be read, the default page will be called in case of
   * Authentication failed
   * 
   * @param path
   *          the path of the page to be called
   * @return the handler itself for chained calls
   */
  public FormLoginHandlerBt setDefaultAuthenticationErrorPage(String path) {
    this.defaultAuthErrorPage = path;
    return this;
  }

  @Override
  public FormLoginHandlerBt setUsernameParam(String usernameParam) {
    this.usernameParam = usernameParam;
    return this;
  }

  @Override
  public FormLoginHandlerBt setPasswordParam(String passwordParam) {
    this.passwordParam = passwordParam;
    return this;
  }

  @Override
  public FormLoginHandlerBt setReturnURLParam(String returnURLParam) {
    this.returnURLParam = returnURLParam;
    return this;
  }

  @Override
  public FormLoginHandler setDirectLoggedInOKURL(String directLoggedInOKURL) {
    this.directLoggedInOKURL = directLoggedInOKURL;
    return this;
  }

  @Override
  public void handle(RoutingContext context) {
    HttpServerRequest req = context.request();
    if (req.method() != HttpMethod.POST) {
      context.fail(405); // Must be a POST
    } else {
      if (!req.isExpectMultipart()) {
        throw new IllegalStateException("Form body not parsed - do you forget to include a BodyHandler?");
      }
      MultiMap params = req.formAttributes();
      String username = params.get(usernameParam);
      String password = params.get(passwordParam);
      if (username == null || password == null) {
        log.warn("No username or password provided in form - did you forget to include a BodyHandler?");
        context.fail(400);
      } else {
        JsonObject authInfo = new JsonObject().put("username", username).put("password", password);
        authProvider.authenticate(authInfo, res -> {
          if (res.succeeded()) {
            User user = res.result();
            context.setUser(user);
            if (redirectBySession(context)) {
              return;
            }
            // Either no session or no return url
            if (!redirectByDirectLoginUrl(context)) {
              // Just show a basic page
              req.response().end(DEFAULT_DIRECT_LOGGED_IN_OK_PAGE);
            }
          } else {
            log.info("authentication failed: " + res.cause());
            handleAuthenticationError(context);
          }
        });
      }
    }
  }

  private void handleAuthenticationError(RoutingContext context) {
    // context.fail(403); // Failed login
    if (redirectBySession(context)) {
      return;
    } else if (defaultAuthErrorPage != null) {
      doRedirect(context.response(), defaultAuthErrorPage);
    } else {
      context.fail(403);
    }
  }

  private boolean redirectBySession(RoutingContext context) {
    if (context.session() != null) {
      String returnURL = context.session().remove(returnURLParam);
      if (returnURL != null) {
        // Now redirect back to the original url
        doRedirect(context.response(), returnURL);
        return true;
      }
    }
    return false;
  }

  private boolean redirectByDirectLoginUrl(RoutingContext context) {
    if (directLoggedInOKURL != null) {
      // Redirect to the default logged in OK page - this would occur
      // if the user logged in directly at this URL without being redirected here first from another
      // url
      doRedirect(context.response(), directLoggedInOKURL);
      return true;
    }
    return false;
  }

  private void doRedirect(HttpServerResponse response, String url) {
    response.putHeader("location", url).setStatusCode(302).end();
  }

  private static final String DEFAULT_DIRECT_LOGGED_IN_OK_PAGE = ""
      + "<html><body><h1>Login successful</h1></body></html>";

}

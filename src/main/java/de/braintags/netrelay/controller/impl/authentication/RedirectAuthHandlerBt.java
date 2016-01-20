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

import de.braintags.netrelay.RequestUtil;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.impl.AuthHandlerImpl;
import io.vertx.ext.web.handler.impl.RedirectAuthHandlerImpl;

/**
 * Implementation of AuthHandler based on vertx {@link RedirectAuthHandlerImpl}, which adds the url parameters into the
 * page, which is aimed after a successful login
 * 
 * @author Michael Remme
 * 
 */
public class RedirectAuthHandlerBt extends AuthHandlerImpl {
  private static final Logger log = LoggerFactory.getLogger(RedirectAuthHandlerImpl.class);

  private final String loginRedirectURL;
  private final String returnURLParam;

  public RedirectAuthHandlerBt(AuthProvider authProvider, String loginRedirectURL, String returnURLParam) {
    super(authProvider);
    this.loginRedirectURL = loginRedirectURL;
    this.returnURLParam = returnURLParam;
  }

  @Override
  public void handle(RoutingContext context) {
    Session session = context.session();
    if (session != null) {
      User user = context.user();
      if (user != null) {
        // Already logged in, just authorise
        authorise(user, context);
      } else {
        // Now redirect to the login url - we'll get redirected back here after successful login
        String url = RequestUtil.createRedirectUrl(context.request(), context.request().path());
        session.put(returnURLParam, url);
        context.response().putHeader("location", loginRedirectURL).setStatusCode(302).end();
      }
    } else {
      context.fail(new NullPointerException("No session - did you forget to include a SessionHandler?"));
    }

  }
}

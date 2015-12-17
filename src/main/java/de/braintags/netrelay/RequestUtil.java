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

import de.braintags.netrelay.model.Member;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.mongo.impl.MongoUser;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;

/**
 * Some Utility methods to handle requests
 * 
 * @author mremme
 * 
 */
public class RequestUtil {
  private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);

  /**
   * Render a specific template
   * 
   * @param context
   * @param path
   * @param contentType
   * @param templateDirectory
   * @param thEngine
   */
  public static void renderSpecificTemplate(RoutingContext context, String path, String contentType,
      String templateDirectory, ThymeleafTemplateEngine thEngine) {
    // templateHandler.handle(context);
    String file = templateDirectory + path;
    thEngine.render(context, file, res -> {
      if (res.succeeded()) {
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, contentType).end(res.result());
      } else {
        context.fail(res.cause());
      }
    });
  }

  /**
   * Sending a redirect to another page
   * 
   * @param response
   * @param path
   */
  public static void sendRedirect(HttpServerResponse response, String path) {
    response.putHeader("location", path);
    response.setStatusCode(302);
    response.end();
  }

  /**
   * Loads the given property from the request
   * 
   * @param context
   * @param propName
   * @param required
   * @param errorObject
   * @return
   */
  public static String loadProperty(RoutingContext context, String propName, boolean required, JsonObject errorObject) {
    String value = context.request().getParam(propName);
    // value = context.request().getFormAttribute(propName);
    if (required && (value == null || value.trim().isEmpty()))
      errorObject.put(propName + "Error", "parameter '" + propName + " is required");
    return value;
  }

  /**
   * Clean one value element of the path within one "/".
   * "/path/2/template.html" - "2" = "/path/template.html"
   * 
   * @param value
   *          the prure value to be removed
   * @param path
   *          the path to be cleaned
   * @return the cleaned path
   * @throws IllegalArgumentException
   *           if element with one "/" before or after the value wasn't found
   */
  public static String cleanPathElement(String value, String path) {
    int index = path.indexOf("/" + value + "/");
    if (index < 0) {
      index = path.indexOf("/" + value);
    }
    if (index < 0) {
      index = path.indexOf(value + "/");
    }
    if (index < 0) {
      throw new IllegalArgumentException("Could not clean url from value '" + value + "'");
    }
    String rpath = path.substring(0, index) + path.substring(index + value.length() + 1);
    return rpath;
  }

  /**
   * This method searches for a logged in User and returns it
   * 
   * @param context
   * @param mongoClient
   * @param collectionName
   * @param resultHandler
   */
  public static final void getCurrentUser(RoutingContext context, MongoClient mongoClient, String collectionName,
      Handler<AsyncResult<Member>> resultHandler) {
    User user = context.user();

    if (user == null) {
      UnsupportedOperationException ex = new UnsupportedOperationException(
          "To call this method a user must be logged in");
      Future<Member> future = Future.failedFuture(ex);
      resultHandler.handle(future);
      return;
    }

    if (user instanceof Member) {
      Future<Member> future = Future.succeededFuture((Member) user);
      resultHandler.handle(future);
      return;
    } else if (user instanceof MongoUser) {
      Future<Member> future = Future.failedFuture(new UnsupportedOperationException());
      resultHandler.handle(future);

      // JsonObject principal = ((MongoUser) user).principal();
      // final Member member = new Member(FairyTaleVerticle.USER_COLLECTION_NAME);
      // member.fromJson(principal, mongoClient, res -> {
      // if (res.failed()) {
      // resultHandler.handle(Future.failedFuture(res.cause()));
      // return;
      // } else {
      // Future<Member> future = Future.succeededFuture(member);
      // resultHandler.handle(future);
      // return;
      // }
      // });
    } else {
      Future<Member> future = Future.failedFuture(new IllegalArgumentException("not an instance of Member"));
      resultHandler.handle(future);
      return;
    }

    // JsonObject principal = context.user().principal();
    // Member.findUserByPrincipal(mongoClient, collectionName, principal,
    // res -> {
    // if (res.failed()) {
    // resultHandler.handle(res);
    // } else {
    // Member tmpUser = res.result();
    // ApexUtil.setCurrentUser(tmpUser, context);
    // Future<Member> future = Future.succeededFuture(tmpUser);
    // resultHandler.handle(future);
    // }
    // });

  }

}

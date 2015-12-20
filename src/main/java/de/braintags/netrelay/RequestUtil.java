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

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.exception.NoSuchRecordException;
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
    if (path.indexOf(value) >= 0) {
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
      path = path.substring(0, index) + path.substring(index + value.length() + 1);
    }
    return path;
  }

  /**
   * This method searches for a logged in User and returns it
   * 
   * @param context
   * @param mongoClient
   * @param collectionName
   * @param resultHandler
   */
  public static final void getCurrentUser(RoutingContext context, IDataStore datastore, Class mapperClass,
      Handler<AsyncResult<Member>> resultHandler) {
    User user = context.user();

    if (user == null) {
      UnsupportedOperationException ex = new UnsupportedOperationException(
          "To call this method a user must be logged in");
      resultHandler.handle(Future.failedFuture(ex));
      return;
    }

    if (user instanceof Member) {
      resultHandler.handle(Future.succeededFuture((Member) user));
    } else if (user instanceof MongoUser) {
      JsonObject principal = user.principal();
      String id = user.principal().getString("_id");
      IQuery<Member> query = datastore.createQuery(mapperClass);
      query.field(query.getMapper().getIdField().getName()).is(id);
      query.execute(qr -> {
        if (qr.failed()) {
          resultHandler.handle(Future.failedFuture(qr.cause()));
        } else {
          if (qr.result().size() <= 0) {
            resultHandler
                .handle(Future.failedFuture(new NoSuchRecordException("no record found for principal with id " + id)));
          }
          qr.result().iterator().next(ir -> {
            if (ir.failed()) {
              resultHandler.handle(Future.failedFuture(ir.cause()));
            } else {
              resultHandler.handle(Future.succeededFuture(ir.result()));
            }
          });
        }
      });
    } else {
      Future<Member> future = Future
          .failedFuture(new UnsupportedOperationException("user type not supported: " + user.getClass().getName()));
      resultHandler.handle(future);
      return;
    }
  }

  /**
   * Get a member, which was previously added during the session
   * 
   * @param context
   * @return
   */
  public static Member getCurrentUser(RoutingContext context) {
    return context.session().get(Member.CURRENT_USER_PROPERTY);
  }

  /**
   * Set the current user as property into the Context session
   * 
   * @param user
   * @param context
   */
  public static final void setCurrentUser(Member user, RoutingContext context) {
    context.session().put(Member.CURRENT_USER_PROPERTY, user);
  }

  /**
   * Remove a current user from the session, if set
   * 
   * @param context
   */
  public static final void removeCurrentUser(RoutingContext context) {
    context.session().remove(Member.CURRENT_USER_PROPERTY);
  }

}

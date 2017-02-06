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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.braintags.vertx.util.ExceptionUtil;
import de.braintags.vertx.util.exception.ParameterRequiredException;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;

/**
 * Some Utility methods to handle requests
 * 
 * @author mremme
 * 
 */
public class RequestUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtil.class);
  /**
   * If a property is defined inside the context with this name, it will be used in case of redirects to be added at the
   * beginning of the path
   */
  public static final String PREPATH_PROPERTY = "netrelay.redirect.prepath";

  private RequestUtil() {
  }

  /**
   * Split the url into path and file without extension
   * 
   * @param requestUrl
   *          the url to be used
   * @return
   */
  public static String[] splitPathFile(String requestUrl) {
    if (requestUrl == null || requestUrl.isEmpty()) {
      throw new IllegalArgumentException("url must not be null or empty");
    }
    if (requestUrl.endsWith("/")) {
      return new String[] { requestUrl, "index" };
    }
    String[] elements = new String[2];
    elements[0] = requestUrl.substring(0, requestUrl.lastIndexOf("/") + 1);
    String tmpName = requestUrl.substring(requestUrl.lastIndexOf("/") + 1);
    if (tmpName.contains(".")) {
      tmpName = tmpName.substring(0, tmpName.indexOf('.'));
    }
    elements[1] = tmpName;
    return elements;
  }

  /**
   * Encodes the text into a suitable format for UTF-8 for http requests for instance
   * 
   * @param text
   *          text to be encoded
   * @return the encoded result
   */
  public static String encodeText(String text) {
    return encodeText(text, "UTF-8");
  }

  /**
   * Encodes the text into a suitable format for http requests for instance
   * 
   * @param text
   *          text to be encoded
   * @param encoding
   *          the encoding used
   * @return the encoded result
   */
  public static String encodeText(String text, String encoding) {
    try {
      return URLEncoder.encode(text, encoding);
    } catch (UnsupportedEncodingException e) {
      throw ExceptionUtil.createRuntimeException(e);
    }
  }

  /**
   * Read the value of the defined key from a request parameter
   * 
   * @param context
   *          the context to be handled
   * @param key
   *          the key to retrive from the request parameters
   * @param defaultValue
   *          will be returned, if value is null
   * @param required
   *          if required and value is null, an exception is thrown
   * @return the value or null, if none and not required
   */
  public static String readParameterAttribute(RoutingContext context, String key, String defaultValue,
      boolean required) {
    String value = context.request().params().get(key);
    if (value == null && required) {
      throw new ParameterRequiredException(key);
    }
    return value == null ? defaultValue : value;
  }

  /**
   * Reads a value from the request
   * 
   * @param context
   *          the context from the current request
   * @param key
   *          the key to search for
   * @param required
   *          is the value required?
   * @return a found value, the default value or null
   * @throws ParameterRequiredException,
   *           if required parameter wasn't found
   */
  public static String readParameter(RoutingContext context, String key, boolean required) {
    return readParameter(context, key, null, required);
  }

  /**
   * Reads a value either from the request or - if not found there - from the context
   * 
   * @param context
   *          the context from the current request
   * @param key
   *          the key to search for
   * @param defaultValue
   *          the default value
   * @param required
   *          is the value required?
   * @return a found value, the default value or null
   * @throws ParameterRequiredException,
   *           if required parameter wasn't found
   */
  public static String readParameterOrContext(RoutingContext context, String key, String defaultValue,
      boolean required) {
    String value = readParameter(context, key, false);
    if (value == null) {
      value = context.get(key);
    }

    if (value == null && required)
      throw new ParameterRequiredException(key);
    return value == null ? defaultValue : value;
  }

  /**
   * Reads a value from the request
   * 
   * @param context
   *          the context from the current request
   * @param key
   *          the key to search for
   * @param defaultValue
   *          the default value
   * @param required
   *          is the value required?
   * @return a found value, the default value or null
   * @throws ParameterRequiredException,
   *           if required parameter wasn't found
   */
  public static String readParameter(RoutingContext context, String key, String defaultValue, boolean required) {
    String value = RequestUtil.readFormAttribute(context, key, null, false);
    if (value == null) {
      value = RequestUtil.readParameterAttribute(context, key, defaultValue, false);
    }
    if (value == null && required)
      throw new ParameterRequiredException(key);
    return value;
  }

  /**
   * Read the value of the defined key from a transferred form request
   * 
   * @param context
   *          the context to be handled
   * @param key
   *          the key to retrive from the form parameters
   * @param defaultValue
   *          will be returned, if value is null
   * @param required
   *          if required and value is null, an exception is thrown
   * @return the value or null, if none and not required
   */
  public static String readFormAttribute(RoutingContext context, String key, String defaultValue, boolean required) {
    String value = context.request().formAttributes().get(key);
    if (value == null && required) {
      throw new ParameterRequiredException(key);
    }
    return value == null ? defaultValue : value;
  }

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
   * The same than {@link #sendRedirect(HttpServerResponse, HttpServerRequest, String, true)}
   * 
   * @param context
   * @param path
   */
  public static void sendRedirect(RoutingContext context, String path) {
    sendRedirect(context, path, true);
  }

  /**
   * Sending a redirect as 302 to another page by adding query parameters of a current request
   * 
   * @param context
   * @param path
   * @param reuseArguments
   *          if true, the query parameters of the current request are reused
   */
  public static void sendRedirect(RoutingContext context, String path, boolean resuseArguments) {
    sendRedirect(context, path, resuseArguments, 302);
  }

  /**
   * Sending a redirect to another page by adding query parameters of a current request
   * 
   * @param context
   * @param path
   * @param reuseArguments
   *          if true, the query parameters of the current request are reused
   *          param code - the http code to be used
   */
  public static void sendRedirect(RoutingContext context, String path, boolean resuseArguments, int code) {
    HttpServerResponse response = context.response();
    LOGGER.info("sending redirect to " + path);
    response.putHeader("location", createRedirectUrl(context, path, resuseArguments));
    response.setStatusCode(code);
    response.end();
  }

  /**
   * The same than {@link #createRedirectUrl(HttpServerRequest, String, true)}
   * 
   * @param context
   * @param path
   * @return
   */
  public static String createRedirectUrl(RoutingContext context, String path) {
    return createRedirectUrl(context, path, true);
  }

  /**
   * Creates a url from the given information for a redirect. If request is not null, then current query parameters are
   * added to the path
   * 
   * @param context
   * @param path
   * @param reuseArguments
   *          if true, the query parameters of the current request are reused
   * @return
   */
  public static String createRedirectUrl(RoutingContext context, String path, boolean reuseArguments) {
    String tmpPath = path;
    HttpServerRequest request = context.request();
    if (request != null && reuseArguments) {
      String qp = request.query();
      if (qp != null && qp.hashCode() != 0) {
        tmpPath += (path.indexOf('?') < 0 ? "?" : "&") + qp;
      }
    }
    String prePath = context.get(PREPATH_PROPERTY);
    if (prePath != null) {
      tmpPath = prePath + ((prePath.endsWith("/") || tmpPath.startsWith("/")) ? "" : "/") + tmpPath;
    }
    return tmpPath;
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
    if (required && (value == null || value.trim().isEmpty()))
      errorObject.put(propName + "Error", "parameter '" + propName + " is required");
    return value;
  }

  /**
   * Clean one value element of the path within one "/".
   * "/path/2/template.html" - "2" = "/path/template.html"
   * 
   * Logic is:
   * value, "/" or nothing before or behind
   * 
   * @param value
   *          the pure value to be removed
   * @param path
   *          the path to be cleaned
   * @return the cleaned path
   * @throws IllegalArgumentException
   *           if element with one "/" before or after the value wasn't found
   */
  public static String cleanPathElement(String value, String path) {
    String[] elements = path.split("/");
    Buffer buffer = Buffer.buffer(path.startsWith("/") ? "/" : "");
    boolean added = false;
    for (String element : elements) {
      if (element != null && element.hashCode() != 0 && !element.equals(value)) {
        buffer.appendString(added ? "/" : "").appendString(element);
        added = true;
      }
    }
    return buffer.appendString(path.endsWith("/") ? "/" : "").toString();
  }

}

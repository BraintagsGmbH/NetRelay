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
package de.braintags.netrelay.controller;

import java.util.Objects;
import java.util.Properties;

import de.braintags.netrelay.routing.RouterDefinition;
import de.braintags.vertx.util.DebugDetection;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;

/**
 * SessionController uses a {@link SessionHandler} internally to implement session handling for all browser sessions
 * 
 * <br>
 * <br>
 * Config-Parameter:<br/>
 * <UL>
 * <LI>{@value #SESSION_STORE_PROP}<br/>
 * <LI>{@value #EXPIRATION_STORE_PROP}<br/>
 * <LI>{@value #SESSION_MAP_NAME_PROP}<br/>
 * </UL>
 * <br>
 * Request-Parameter:<br/>
 * <br/>
 * Result-Parameter:<br/>
 * <br/>
 * 
 * @author Michael Remme
 */
public class SessionController extends AbstractController {
  /**
   * The name of the property which defines, which {@link io.vertx.ext.web.sstore.SessionStore} shall be used.
   * References to {@link SessionStore}. Possible values are {@link SessionStore#LOCAL_SESSION_STORE} and
   * {@link SessionStore#CLUSTERED_SESSION_STORE}
   */
  public static final String SESSION_STORE_PROP = "sessionStore";
  /**
   * The name of the property, which defines in a time unit, when a session expires. Possible definitions are:
   * 30000 = milliseconds
   * 30 m = 30 minutes
   */
  public static final String EXPIRATION_STORE_PROP = "expiration";

  /**
   * The name of the property which defines the name of the Map, where inside sessions are stored
   */
  public static final String SESSION_MAP_NAME_PROP = "sessionMapName";

  /**
   * The default time, when a session expires in milliseconds
   */
  public static final String DEFAULT_SESSION_EXPIRATION = "30 m";

  private SessionHandler sessionHandler;

  /**
   * 
   */
  public SessionController() {
  }

  @Override
  public void initProperties(Properties properties) {
    String storeDef = (String) properties.get(SESSION_STORE_PROP);
    Objects.requireNonNull(storeDef);
    SessionStore store = SessionStore.valueOf(storeDef);
    switch (store) {
    case LOCAL_SESSION_STORE:
      sessionHandler = SessionHandler
            .create(LocalSessionStore.create(getVertx(), getSessionMapName(properties), parseExpiration(properties)))
            .setCookieHttpOnlyFlag(true).setCookieSecureFlag(!DebugDetection.isTest());
        break;

    case CLUSTERED_SESSION_STORE:
      sessionHandler = SessionHandler.create(ClusteredSessionStore.create(getVertx(), getSessionMapName(properties)));
      break;

    default:
      throw new UnsupportedOperationException(store.toString());
    }
  }

  private long parseExpiration(Properties properties) {
    String timeString = (String) properties.getOrDefault(EXPIRATION_STORE_PROP, DEFAULT_SESSION_EXPIRATION);
    if (timeString.endsWith("m")) {
      return Long.parseLong(timeString.substring(0, timeString.length() - 1).trim()) * 60 * 1000;
    } else {
      return Long.parseLong(timeString);
    }
  }

  private String getSessionMapName(Properties properties) {
    return (String) properties.getOrDefault(SESSION_MAP_NAME_PROP, "sessionMap");
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handleController(RoutingContext context) {
    sessionHandler.handle(context);
  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName(SessionController.class.getSimpleName());
    def.setBlocking(false);
    def.setController(SessionController.class);
    def.setHandlerProperties(getDefaultProperties());
    return def;
  }

  /**
   * Get the default properties for an implementation of StaticController
   * 
   * @return
   */
  public static Properties getDefaultProperties() {
    Properties json = new Properties();
    json.put(SESSION_STORE_PROP, SessionStore.LOCAL_SESSION_STORE.toString());
    json.put(EXPIRATION_STORE_PROP, String.valueOf(DEFAULT_SESSION_EXPIRATION));
    return json;
  }

}

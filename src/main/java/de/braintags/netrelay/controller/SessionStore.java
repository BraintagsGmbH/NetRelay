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

import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;

/**
 * Enumeration for different types of {@link io.vertx.ext.web.sstore.SessionStore}
 * 
 * @author Michael Remme
 * 
 */
public enum SessionStore {

  LOCAL_SESSION_STORE(LocalSessionStore.class),
  CLUSTERED_SESSION_STORE(ClusteredSessionStore.class);

  private Class<? extends io.vertx.ext.web.sstore.SessionStore> sessionStoreClass;

  private SessionStore(Class<? extends io.vertx.ext.web.sstore.SessionStore> storeClass) {
    this.sessionStoreClass = storeClass;
  }

  /**
   * Get the defined implementation class of {@link io.vertx.ext.web.sstore.SessionStore} for the given definition
   * 
   * @return the sessionStoreClass
   */
  public final Class<? extends io.vertx.ext.web.sstore.SessionStore> getSessionStoreClass() {
    return sessionStoreClass;
  }

}

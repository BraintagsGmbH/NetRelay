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
package de.braintags.netrelay.util;

import java.net.URI;
import java.util.concurrent.ConcurrentSkipListSet;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.impl.RoutingContextImpl;

/**
 * A dummy implementation for use with applications, which need to call an IController
 * 
 * @author Michael Remme
 * 
 */
public class MockRoutingContext extends RoutingContextImpl {

  /**
   * @param mountPoint
   * @param router
   * @param request
   * @param routes
   */
  public MockRoutingContext(Vertx vertx, URI uri) {
    super(null, null, createRequest(vertx, uri), new ConcurrentSkipListSet<>());
  }

  private static final HttpServerRequest createRequest(Vertx vertx, URI uri) {
    return new MockHttpServerRequest(uri, new MockHttpServerResponse());
  }

}

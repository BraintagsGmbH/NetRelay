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
  private Vertx vertx;
  private boolean exceptionOnFail = true;

  private boolean failed;
  private int statusCode;
  private Throwable exception;

  public MockRoutingContext(Vertx vertx, URI uri) {
    this(vertx, uri, true);
  }

  public MockRoutingContext(Vertx vertx, URI uri, boolean exceptionOnFail) {
    this(vertx, createRequest(vertx, uri), exceptionOnFail);
  }

  public MockRoutingContext(Vertx vertx, HttpServerRequest request, boolean exceptionOnFail) {
    super(null, null, request, new ConcurrentSkipListSet<>());
    this.vertx = vertx;
    this.exceptionOnFail = exceptionOnFail;
  }

  private static final HttpServerRequest createRequest(Vertx vertx, URI uri) {
    return new MockHttpServerRequest(uri, new MockHttpServerResponse());
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.ext.web.impl.RoutingContextImpl#vertx()
   */
  @Override
  public Vertx vertx() {
    return vertx;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.ext.web.impl.RoutingContextImpl#fail(int)
   */
  @Override
  public void fail(int statusCode) {
    this.statusCode = statusCode;
    doFail();
  }

  private void doFail() {
    failed = true;
    if (exceptionOnFail) {
      if (exception != null) {
        throw new RuntimeException("Mock routing context failed", exception);
      } else {
        throw new RuntimeException("Mock routing context failed");
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.ext.web.impl.RoutingContextImpl#fail(java.lang.Throwable)
   */
  @Override
  public void fail(Throwable t) {
    this.exception = t;
    doFail();
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.ext.web.impl.RoutingContextImpl#statusCode()
   */
  @Override
  public int statusCode() {
    return statusCode;
  }

  public Throwable getException() {
    return exception;
  }

  public boolean isFailed() {
    return failed;
  }

}

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

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerFileUpload;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MockHttpServerRequest implements HttpServerRequest {
  private final HttpServerResponse response;
  private MultiMap params = MultiMap.caseInsensitiveMultiMap();
  private MultiMap headers = MultiMap.caseInsensitiveMultiMap();
  private MultiMap attributes = MultiMap.caseInsensitiveMultiMap();

  private URI uri;

  /**
   * 
   */
  public MockHttpServerRequest(URI uri, HttpServerResponse response) {
    this.uri = uri;
    this.response = response;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#exceptionHandler(io.vertx.core.Handler)
   */
  @Override
  public HttpServerRequest exceptionHandler(Handler<Throwable> handler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#handler(io.vertx.core.Handler)
   */
  @Override
  public HttpServerRequest handler(Handler<Buffer> handler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#pause()
   */
  @Override
  public HttpServerRequest pause() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#resume()
   */
  @Override
  public HttpServerRequest resume() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#endHandler(io.vertx.core.Handler)
   */
  @Override
  public HttpServerRequest endHandler(Handler<Void> endHandler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#version()
   */
  @Override
  public HttpVersion version() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#method()
   */
  @Override
  public HttpMethod method() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#uri()
   */
  @Override
  public String uri() {
    return uri.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#path()
   */
  @Override
  public String path() {
    return uri.getPath();
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#query()
   */
  @Override
  public @Nullable String query() {
    return uri.getQuery();
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#response()
   */
  @Override
  public HttpServerResponse response() {
    return response;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#headers()
   */
  @Override
  public MultiMap headers() {
    return headers;
  }

  @Override
  public String getHeader(String headerName) {
    return headers().get(headerName);
  }

  @Override
  public String getHeader(CharSequence headerName) {
    return headers().get(headerName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#params()
   */
  @Override
  public MultiMap params() {
    return params;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#getParam(java.lang.String)
   */
  @Override
  public @Nullable String getParam(String paramName) {
    return params().get(paramName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#remoteAddress()
   */
  @Override
  public SocketAddress remoteAddress() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#localAddress()
   */
  @Override
  public SocketAddress localAddress() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#peerCertificateChain()
   */
  @Override
  public X509Certificate[] peerCertificateChain() throws SSLPeerUnverifiedException {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#absoluteURI()
   */
  @Override
  public String absoluteURI() {
    return uri.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#bodyHandler(io.vertx.core.Handler)
   */
  @Override
  public HttpServerRequest bodyHandler(@Nullable Handler<Buffer> bodyHandler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#netSocket()
   */
  @Override
  public NetSocket netSocket() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#setExpectMultipart(boolean)
   */
  @Override
  public HttpServerRequest setExpectMultipart(boolean expect) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#isExpectMultipart()
   */
  @Override
  public boolean isExpectMultipart() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#uploadHandler(io.vertx.core.Handler)
   */
  @Override
  public HttpServerRequest uploadHandler(@Nullable Handler<HttpServerFileUpload> uploadHandler) {
    return null;
  }

  @Override
  public MultiMap formAttributes() {
    return attributes;
  }

  @Override
  public String getFormAttribute(String attributeName) {
    return formAttributes().get(attributeName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#upgrade()
   */
  @Override
  public ServerWebSocket upgrade() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#isEnded()
   */
  @Override
  public boolean isEnded() {
    return false;
  }

}

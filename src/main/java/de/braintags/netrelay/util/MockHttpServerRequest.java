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
import javax.net.ssl.SSLSession;
import javax.security.cert.X509Certificate;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpFrame;
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
  private final MultiMap params = MultiMap.caseInsensitiveMultiMap();
  private final MultiMap headers = MultiMap.caseInsensitiveMultiMap();
  private final MultiMap attributes = MultiMap.caseInsensitiveMultiMap();

  private final URI uri;
  private final HttpMethod method;

  public MockHttpServerRequest(final URI uri, final HttpServerResponse response) {
    this(uri, null, response);
  }

  public MockHttpServerRequest(final URI uri, final HttpMethod method, final HttpServerResponse response) {
    this.uri = uri;
    this.method = method;
    this.response = response;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#exceptionHandler(io.vertx.core.Handler)
   */
  @Override
  public HttpServerRequest exceptionHandler(final Handler<Throwable> handler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#handler(io.vertx.core.Handler)
   */
  @Override
  public HttpServerRequest handler(final Handler<Buffer> handler) {
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
  public HttpServerRequest endHandler(final Handler<Void> endHandler) {
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
    return this.method;
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
    String path = uri.getPath();
    return path == null || path.hashCode() == 0 ? "/" : path;
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
  public String getHeader(final String headerName) {
    return headers().get(headerName);
  }

  @Override
  public String getHeader(final CharSequence headerName) {
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
  public @Nullable String getParam(final String paramName) {
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
  public HttpServerRequest bodyHandler(@Nullable final Handler<Buffer> bodyHandler) {
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
  public HttpServerRequest setExpectMultipart(final boolean expect) {
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
  public HttpServerRequest uploadHandler(@Nullable final Handler<HttpServerFileUpload> uploadHandler) {
    return null;
  }

  @Override
  public MultiMap formAttributes() {
    return attributes;
  }

  @Override
  public String getFormAttribute(final String attributeName) {
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

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#isSSL()
   */
  @Override
  public boolean isSSL() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#rawMethod()
   */
  @Override
  public String rawMethod() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#scheme()
   */
  @Override
  public @Nullable String scheme() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#host()
   */
  @Override
  public @Nullable String host() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#customFrameHandler(io.vertx.core.Handler)
   */
  @Override
  public HttpServerRequest customFrameHandler(final Handler<HttpFrame> handler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerRequest#connection()
   */
  @Override
  public HttpConnection connection() {
    return null;
  }

  @Override
  public SSLSession sslSession() {
    // TODO Auto-generated method stub
    return null;
  }

}

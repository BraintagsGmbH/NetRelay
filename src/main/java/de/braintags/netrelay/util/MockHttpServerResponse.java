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

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MockHttpServerResponse implements HttpServerResponse {

  /**
   * 
   */
  public MockHttpServerResponse() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.streams.WriteStream#writeQueueFull()
   */
  @Override
  public boolean writeQueueFull() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#exceptionHandler(io.vertx.core.Handler)
   */
  @Override
  public HttpServerResponse exceptionHandler(Handler<Throwable> handler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#write(io.vertx.core.buffer.Buffer)
   */
  @Override
  public HttpServerResponse write(Buffer data) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#setWriteQueueMaxSize(int)
   */
  @Override
  public HttpServerResponse setWriteQueueMaxSize(int maxSize) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#drainHandler(io.vertx.core.Handler)
   */
  @Override
  public HttpServerResponse drainHandler(Handler<Void> handler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#getStatusCode()
   */
  @Override
  public int getStatusCode() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#setStatusCode(int)
   */
  @Override
  public HttpServerResponse setStatusCode(int statusCode) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#getStatusMessage()
   */
  @Override
  public String getStatusMessage() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#setStatusMessage(java.lang.String)
   */
  @Override
  public HttpServerResponse setStatusMessage(String statusMessage) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#setChunked(boolean)
   */
  @Override
  public HttpServerResponse setChunked(boolean chunked) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#isChunked()
   */
  @Override
  public boolean isChunked() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#headers()
   */
  @Override
  public MultiMap headers() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#putHeader(java.lang.String, java.lang.String)
   */
  @Override
  public HttpServerResponse putHeader(String name, String value) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#putHeader(java.lang.CharSequence, java.lang.CharSequence)
   */
  @Override
  public HttpServerResponse putHeader(CharSequence name, CharSequence value) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#putHeader(java.lang.String, java.lang.Iterable)
   */
  @Override
  public HttpServerResponse putHeader(String name, Iterable<String> values) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#putHeader(java.lang.CharSequence, java.lang.Iterable)
   */
  @Override
  public HttpServerResponse putHeader(CharSequence name, Iterable<CharSequence> values) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#trailers()
   */
  @Override
  public MultiMap trailers() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#putTrailer(java.lang.String, java.lang.String)
   */
  @Override
  public HttpServerResponse putTrailer(String name, String value) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#putTrailer(java.lang.CharSequence, java.lang.CharSequence)
   */
  @Override
  public HttpServerResponse putTrailer(CharSequence name, CharSequence value) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#putTrailer(java.lang.String, java.lang.Iterable)
   */
  @Override
  public HttpServerResponse putTrailer(String name, Iterable<String> values) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#putTrailer(java.lang.CharSequence, java.lang.Iterable)
   */
  @Override
  public HttpServerResponse putTrailer(CharSequence name, Iterable<CharSequence> value) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#closeHandler(io.vertx.core.Handler)
   */
  @Override
  public HttpServerResponse closeHandler(@Nullable Handler<Void> handler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#write(java.lang.String, java.lang.String)
   */
  @Override
  public HttpServerResponse write(String chunk, String enc) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#write(java.lang.String)
   */
  @Override
  public HttpServerResponse write(String chunk) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#writeContinue()
   */
  @Override
  public HttpServerResponse writeContinue() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#end(java.lang.String)
   */
  @Override
  public void end(String chunk) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#end(java.lang.String, java.lang.String)
   */
  @Override
  public void end(String chunk, String enc) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#end(io.vertx.core.buffer.Buffer)
   */
  @Override
  public void end(Buffer chunk) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#end()
   */
  @Override
  public void end() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#sendFile(java.lang.String, long, long)
   */
  @Override
  public HttpServerResponse sendFile(String filename, long offset, long length) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#sendFile(java.lang.String, long, long, io.vertx.core.Handler)
   */
  @Override
  public HttpServerResponse sendFile(String filename, long offset, long length,
      Handler<AsyncResult<Void>> resultHandler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#close()
   */
  @Override
  public void close() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#ended()
   */
  @Override
  public boolean ended() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#closed()
   */
  @Override
  public boolean closed() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#headWritten()
   */
  @Override
  public boolean headWritten() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#headersEndHandler(io.vertx.core.Handler)
   */
  @Override
  public HttpServerResponse headersEndHandler(@Nullable Handler<Void> handler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#bodyEndHandler(io.vertx.core.Handler)
   */
  @Override
  public HttpServerResponse bodyEndHandler(@Nullable Handler<Void> handler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#bytesWritten()
   */
  @Override
  public long bytesWritten() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#streamId()
   */
  @Override
  public int streamId() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#push(io.vertx.core.http.HttpMethod, java.lang.String, java.lang.String,
   * io.vertx.core.Handler)
   */
  @Override
  public HttpServerResponse push(HttpMethod method, String host, String path,
      Handler<AsyncResult<HttpServerResponse>> handler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#push(io.vertx.core.http.HttpMethod, java.lang.String,
   * io.vertx.core.MultiMap, io.vertx.core.Handler)
   */
  @Override
  public HttpServerResponse push(HttpMethod method, String path, MultiMap headers,
      Handler<AsyncResult<HttpServerResponse>> handler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#push(io.vertx.core.http.HttpMethod, java.lang.String,
   * io.vertx.core.Handler)
   */
  @Override
  public HttpServerResponse push(HttpMethod method, String path, Handler<AsyncResult<HttpServerResponse>> handler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#push(io.vertx.core.http.HttpMethod, java.lang.String, java.lang.String,
   * io.vertx.core.MultiMap, io.vertx.core.Handler)
   */
  @Override
  public HttpServerResponse push(HttpMethod method, String host, String path, MultiMap headers,
      Handler<AsyncResult<HttpServerResponse>> handler) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#reset(long)
   */
  @Override
  public void reset(long code) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.http.HttpServerResponse#writeCustomFrame(int, int, io.vertx.core.buffer.Buffer)
   */
  @Override
  public HttpServerResponse writeCustomFrame(int type, int flags, Buffer payload) {
    return null;
  }

}

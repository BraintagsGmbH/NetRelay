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

import java.util.function.Consumer;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import de.braintags.io.vertx.pojomapper.testdatastore.TestHelper;
import de.braintags.netrelay.impl.NetRelayExt_InternalSettings;
import de.braintags.netrelay.init.Settings;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@RunWith(VertxUnitRunner.class)
public class NetRelayBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(NetRelayBaseTest.class);

  protected static Vertx vertx;
  protected static HttpClient client;
  private static NetRelay netRelay;

  @Rule
  public Timeout rule = Timeout.seconds(Integer.parseInt(System.getProperty("testTimeout", "5")));
  @Rule
  public TestName name = new TestName();

  @Test
  public void testSimpleRoute(TestContext context) throws Exception {
    testRequest(context, HttpMethod.GET, "/index.html", 200, "OK");
  }

  @Before
  public final void initBeforeTest(TestContext context) {
    LOGGER.info("Starting test: " + this.getClass().getSimpleName() + "#" + name.getMethodName());
    initNetRelay(context);
    initTest();
  }

  public void initTest() {

  }

  @BeforeClass
  public static void startup(TestContext context) throws Exception {
    LOGGER.debug("starting class");
    vertx = Vertx.vertx(getOptions());
    client = vertx.createHttpClient(new HttpClientOptions().setDefaultPort(8080));
  }

  @AfterClass
  public static void shutdown(TestContext context) throws Exception {
    LOGGER.debug("performing shutdown");
    netRelay.stop();

    // Async as = context.async();
    // Future<Void> stopFuture = Future.future();
    // stopFuture.setHandler(stRes -> {
    // if (stRes.failed()) {
    // LOGGER.error("Error occured on shutdown", stRes.cause());
    // as.complete();
    // } else {
    // as.complete();
    // }
    // });
    // netRelay.stop(stopFuture);
    // as.await();

    if (vertx != null) {
      Async async = context.async();
      vertx.close(ar -> {
        async.complete();
      });
      async.awaitSuccess();
    }
  }

  protected static VertxOptions getOptions() {
    return TestHelper.getOptions();
  }

  public void initNetRelay(TestContext context) {
    if (netRelay == null) {
      LOGGER.info("init NetRelay");
      Async async = context.async();
      netRelay = createNetRelay();
      vertx.deployVerticle(netRelay, result -> {
        try {
          if (result.failed()) {
            context.fail(result.cause());
          } else {

          }
        } finally {
          async.complete();
        }
      });
      async.awaitSuccess();
    }
  }

  public NetRelay createNetRelay() {
    NetRelayExt_InternalSettings netrelay = new NetRelayExt_InternalSettings();
    modifySettings(netrelay.getSettings());
    return netrelay;
  }

  protected void modifySettings(Settings settings) {
    settings.getDatastoreSettings().setDatabaseName(getClass().getSimpleName());
    settings.getRouterDefinitions()
  }

  protected void testRequest(TestContext context, HttpMethod method, String path, int statusCode, String statusMessage)
      throws Exception {
    testRequest(context, method, path, null, statusCode, statusMessage, null);
  }

  protected void testRequest(TestContext context, HttpMethod method, String path, int statusCode, String statusMessage,
      String responseBody) throws Exception {
    testRequest(context, method, path, null, statusCode, statusMessage, responseBody);
  }

  protected void testRequest(TestContext context, HttpMethod method, String path, int statusCode, String statusMessage,
      Buffer responseBody) throws Exception {
    testRequestBuffer(context, method, path, null, null, statusCode, statusMessage, responseBody);
  }

  protected void testRequestWithContentType(TestContext context, HttpMethod method, String path, String contentType,
      int statusCode, String statusMessage) throws Exception {
    testRequest(context, method, path, req -> req.putHeader("content-type", contentType), statusCode, statusMessage,
        null);
  }

  protected void testRequestWithAccepts(TestContext context, HttpMethod method, String path, String accepts,
      int statusCode, String statusMessage) throws Exception {
    testRequest(context, method, path, req -> req.putHeader("accept", accepts), statusCode, statusMessage, null);
  }

  protected void testRequestWithCookies(TestContext context, HttpMethod method, String path, String cookieHeader,
      int statusCode, String statusMessage) throws Exception {
    testRequest(context, method, path, req -> req.putHeader("cookie", cookieHeader), statusCode, statusMessage, null);
  }

  protected void testRequest(TestContext context, HttpMethod method, String path,
      Consumer<HttpClientRequest> requestAction, int statusCode, String statusMessage, String responseBody)
          throws Exception {
    testRequest(context, method, path, requestAction, null, statusCode, statusMessage, responseBody);
  }

  protected void testRequest(TestContext context, HttpMethod method, String path,
      Consumer<HttpClientRequest> requestAction, Consumer<HttpClientResponse> responseAction, int statusCode,
      String statusMessage, String responseBody) throws Exception {
    testRequestBuffer(context, method, path, requestAction, responseAction, statusCode, statusMessage,
        responseBody != null ? Buffer.buffer(responseBody) : null);
  }

  protected void testRequestBuffer(TestContext context, HttpMethod method, String path,
      Consumer<HttpClientRequest> requestAction, Consumer<HttpClientResponse> responseAction, int statusCode,
      String statusMessage, Buffer responseBodyBuffer) throws Exception {
    testRequestBuffer(context, client, method, 8080, path, requestAction, responseAction, statusCode, statusMessage,
        responseBodyBuffer);
  }

  protected void testRequestBuffer(TestContext context, HttpClient client, HttpMethod method, int port, String path,
      Consumer<HttpClientRequest> requestAction, Consumer<HttpClientResponse> responseAction, int statusCode,
      String statusMessage, Buffer responseBodyBuffer) throws Exception {
    Async async = context.async();
    HttpClientRequest req = client.request(method, port, "localhost", path, resp -> {
      context.assertEquals(statusCode, resp.statusCode());
      context.assertEquals(statusMessage, resp.statusMessage());
      if (responseAction != null) {
        responseAction.accept(resp);
      }
      if (responseBodyBuffer == null) {
        async.complete();
      } else {
        resp.bodyHandler(buff -> {
          context.assertEquals(responseBodyBuffer, buff);
          async.complete();
        });
      }
    });
    if (requestAction != null) {
      requestAction.accept(req);
    }
    req.end();
    async.await();
  }

}

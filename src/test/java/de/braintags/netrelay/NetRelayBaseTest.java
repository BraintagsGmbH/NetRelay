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
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import de.braintags.io.vertx.pojomapper.testdatastore.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.testdatastore.TestHelper;
import de.braintags.io.vertx.util.ErrorObject;
import de.braintags.io.vertx.util.ResultObject;
import de.braintags.netrelay.controller.impl.ThymeleafTemplateController;
import de.braintags.netrelay.impl.NetRelayExt_InternalSettings;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.routing.RouterDefinition;
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
 * NetRelayBaseTest is initializing an internal instance of NetRelay with the default settings.
 * If you want to modifiy the settings, overwrite the method {@link #modifySettings(Settings)}
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
  protected static NetRelay netRelay;

  @Rule
  public Timeout rule = Timeout.seconds(Integer.parseInt(System.getProperty("testTimeout", "5")));
  @Rule
  public TestName name = new TestName();

  @Before
  public final void initBeforeTest(TestContext context) {
    LOGGER.info("Starting test: " + this.getClass().getSimpleName() + "#" + name.getMethodName());
    initNetRelay(context);
    DatastoreBaseTest.EXTERNAL_DATASTORE = netRelay.getDatastore();
    initTest(context);
  }

  public void initTest(TestContext context) {

  }

  @BeforeClass
  public static void startup(TestContext context) throws Exception {
    LOGGER.debug("starting class");
    vertx = Vertx.vertx(getVertxOptions());
    client = vertx.createHttpClient(new HttpClientOptions().setDefaultPort(8080));
  }

  @AfterClass
  public static void shutdown(TestContext context) throws Exception {
    LOGGER.debug("performing shutdown");
    netRelay.stop();
    netRelay = null;

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
        vertx = null;
        async.complete();
      });
      async.awaitSuccess();
    }
  }

  protected static VertxOptions getVertxOptions() {
    return TestHelper.getOptions();
  }

  public void initNetRelay(TestContext context) {
    if (netRelay == null) {
      LOGGER.info("init NetRelay");
      Async async = context.async();
      netRelay = createNetRelay(context);
      vertx.deployVerticle(netRelay, result -> {
        if (result.failed()) {
          context.fail(result.cause());
          async.complete();
        } else {
          async.complete();
        }
      });
      async.awaitSuccess();
    }
  }

  public NetRelay createNetRelay(TestContext context) {
    NetRelayExt_InternalSettings netrelay = new NetRelayExt_InternalSettings();
    modifySettings(context, netrelay.getSettings());
    return netrelay;
  }

  /**
   * This method is modifying the {@link Settings} which are used to init NetRelay. Here it defines the
   * template directory as "testTemplates"
   * 
   * @param settings
   */
  protected void modifySettings(TestContext context, Settings settings) {
    settings.getDatastoreSettings().setDatabaseName(getClass().getSimpleName());
    RouterDefinition def = settings.getRouterDefinitions()
        .getNamedDefinition(ThymeleafTemplateController.class.getSimpleName());
    if (def != null) {
      def.getHandlerProperties().setProperty(ThymeleafTemplateController.TEMPLATE_DIRECTORY_PROPERTY, "testTemplates");
    }
  }

  protected final void testRequest(TestContext context, HttpMethod method, String path, int statusCode,
      String statusMessage) throws Exception {
    testRequest(context, method, path, null, statusCode, statusMessage, null);
  }

  protected final void testRequest(TestContext context, HttpMethod method, String path, int statusCode,
      String statusMessage, String responseBody) throws Exception {
    testRequest(context, method, path, null, statusCode, statusMessage, responseBody);
  }

  protected final void testRequest(TestContext context, HttpMethod method, String path, int statusCode,
      String statusMessage, Buffer responseBody) throws Exception {
    testRequestBuffer(context, method, path, null, null, statusCode, statusMessage, responseBody);
  }

  protected final void testRequestWithContentType(TestContext context, HttpMethod method, String path,
      String contentType, int statusCode, String statusMessage) throws Exception {
    testRequest(context, method, path, req -> req.putHeader("content-type", contentType), statusCode, statusMessage,
        null);
  }

  protected final void testRequestWithAccepts(TestContext context, HttpMethod method, String path, String accepts,
      int statusCode, String statusMessage) throws Exception {
    testRequest(context, method, path, req -> req.putHeader("accept", accepts), statusCode, statusMessage, null);
  }

  protected final void testRequestWithCookies(TestContext context, HttpMethod method, String path, String cookieHeader,
      int statusCode, String statusMessage) throws Exception {
    testRequest(context, method, path, req -> req.putHeader("cookie", cookieHeader), statusCode, statusMessage, null);
  }

  protected final void testRequest(TestContext context, HttpMethod method, String path,
      Consumer<HttpClientRequest> requestAction, int statusCode, String statusMessage, String responseBody)
          throws Exception {
    testRequest(context, method, path, requestAction, null, statusCode, statusMessage, responseBody);
  }

  protected final void testRequest(TestContext context, HttpMethod method, String path,
      Consumer<HttpClientRequest> requestAction, Consumer<ResponseCopy> responseAction, int statusCode,
      String statusMessage, String responseBody) throws Exception {
    testRequestBuffer(context, method, path, requestAction, responseAction, statusCode, statusMessage,
        responseBody != null ? Buffer.buffer(responseBody) : null);
  }

  protected final void testRequestBuffer(TestContext context, HttpMethod method, String path,
      Consumer<HttpClientRequest> requestAction, Consumer<ResponseCopy> responseAction, int statusCode,
      String statusMessage, Buffer responseBodyBuffer) throws Exception {
    testRequestBuffer(context, client, method, 8080, path, requestAction, responseAction, statusCode, statusMessage,
        responseBodyBuffer);
  }

  protected final void testRequestBufferXX(TestContext context, HttpClient client, HttpMethod method, int port,
      String path, Consumer<HttpClientRequest> requestAction, Consumer<HttpClientResponse> responseAction,
      int statusCode, String statusMessage, Buffer responseBodyBuffer) throws Exception {
    Async async = context.async(2);
    ErrorObject<Exception> err = new ErrorObject<>(null);
    HttpClientRequest req = client.request(method, port, "localhost", path, resp -> {
      context.assertEquals(statusCode, resp.statusCode());
      context.assertEquals(statusMessage, resp.statusMessage());
      if (responseAction != null) {
        try {
          responseAction.accept(resp);
        } catch (Exception e) {
          err.setThrowable(e);
        } finally {
          async.countDown();
        }
      } else {
        async.countDown();
      }
      if (responseBodyBuffer == null) {
        async.countDown();
      } else {
        resp.bodyHandler(buff -> {
          context.assertEquals(responseBodyBuffer, buff);
          async.countDown();
        });
      }
    });
    if (requestAction != null) {
      requestAction.accept(req);
    }
    req.end();
    async.await();
    if (err.isError()) {
      context.fail(err.getThrowable());
    }
  }

  protected final void testRequestBuffer(TestContext context, HttpClient client, HttpMethod method, int port,
      String path, Consumer<HttpClientRequest> requestAction, Consumer<ResponseCopy> responseAction, int statusCode,
      String statusMessage, Buffer responseBodyBuffer) throws Exception {
    Async async = context.async();
    ResultObject<ResponseCopy> resultObject = new ResultObject<>(null);
    HttpClientRequest req = client.request(method, port, "localhost", path, resp -> {
      ResponseCopy rc = new ResponseCopy();
      resp.bodyHandler(buff -> {
        rc.content = buff.toString();
        rc.code = resp.statusCode();
        rc.statusMessage = resp.statusMessage();

        resultObject.setResult(rc);
        async.complete();
      });
    });
    if (requestAction != null) {
      requestAction.accept(req);
    }
    req.end();
    async.await();

    ResponseCopy rc = resultObject.getResult();
    context.assertEquals(statusCode, rc.code);
    context.assertEquals(statusMessage, rc.statusMessage);
    if (responseAction != null) {
      responseAction.accept(rc);
    }
    if (responseBodyBuffer == null) {
      // async.complete();
    } else {
      context.assertEquals(responseBodyBuffer.toString(), rc.content);
    }
  }

  protected RouterDefinition defineRouterDefinition(Class controllerClass, String route) {
    RouterDefinition rd = new RouterDefinition();
    rd.setName(controllerClass.getSimpleName());
    rd.setController(controllerClass);
    rd.setRoutes(new String[] { route });
    return rd;
  }

  public class ResponseCopy {
    String content;
    int code;
    String statusMessage;
  }

}

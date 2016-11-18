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
package de.braintags.netrelay.unit;

import java.util.List;
import java.util.function.Consumer;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import de.braintags.io.vertx.keygenerator.KeyGeneratorSettings;
import de.braintags.io.vertx.keygenerator.KeyGeneratorVerticle;
import de.braintags.io.vertx.keygenerator.impl.DebugGenerator;
import de.braintags.io.vertx.pojomapper.testdatastore.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.testdatastore.TestHelper;
import de.braintags.io.vertx.util.ResultObject;
import de.braintags.io.vertx.util.exception.InitException;
import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.impl.NetRelayExt_InternalSettings;
import de.braintags.netrelay.init.MailClientSettings;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.mail.StartTLSOptions;
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

  /**
   * The name of the property, by which the port of NetRelay can be specified
   */
  public static final String SERVER_PORT_PROPERTY = "NetRelayPort";

  public static final String TESTS_MAIL_RECIPIENT = "mremme@braintags.de";
  public static final String TESTS_MAIL_FROM = "netrelayTesting@braintags.de";

  protected static Vertx vertx;
  protected static HttpClient client;
  protected static NetRelay netRelay;
  protected KeyGeneratorVerticle keyGenVerticle;

  public static String HOSTNAME = "localhost";
  public static int PORT = 8080;

  static {
    String portString = System.getProperty(SERVER_PORT_PROPERTY, null);
    if (portString != null) {
      PORT = Integer.parseInt(portString);
    }
  }

  @Rule
  public Timeout rule = Timeout.seconds(Integer.parseInt(System.getProperty("testTimeout", "20")));
  @Rule
  public TestName name = new TestName();

  @Before
  public void initBeforeTest(TestContext context) {
    LOGGER.info("Starting test: " + this.getClass().getSimpleName() + "#" + name.getMethodName());
    initNetRelay(context);
    DatastoreBaseTest.EXTERNAL_DATASTORE = netRelay.getDatastore();
    if (keyGenVerticle == null) {
      LOGGER.info("init Keygenerator");
      Async async = context.async();
      keyGenVerticle = createKeyGenerator(context);
      vertx.deployVerticle(keyGenVerticle, result -> {
        if (result.failed()) {
          context.fail(result.cause());
          async.complete();
        } else {
          async.complete();
        }
      });
      async.awaitSuccess();
    }
    initTest(context);
  }

  public void initTest(TestContext context) {

  }

  @Before
  public final void afterTest(TestContext context) {
    stopTest(context);
  }

  protected void stopTest(TestContext context) {
  }

  protected void undeployVerticle(TestContext context, AbstractVerticle verticle) {
    LOGGER.debug("undeploying verticle " + verticle.deploymentID());
    Async async = context.async();
    vertx.undeploy(verticle.deploymentID(), result -> {
      if (result.failed()) {
        LOGGER.error(result.cause());
        context.fail(result.cause());
        async.complete();
      } else {
        LOGGER.debug("succeeded undeploying verticle " + verticle.deploymentID());
        async.complete();
      }
    });
    async.awaitSuccess();
    LOGGER.debug("finished undeploying verticle " + verticle.deploymentID());
  }

  @BeforeClass
  public static void startup(TestContext context) throws Exception {
    LOGGER.debug("starting class");
    vertx = Vertx.vertx(getVertxOptions());
    client = vertx.createHttpClient(new HttpClientOptions().setDefaultPort(8080));
    // boolean startMongoLocal = Boolean.getBoolean("startMongoLocal");
    // String portString = System.getProperty(MongoDataStoreInit.LOCAL_PORT_PROP, "27017");
    // int port = Integer.parseInt(portString);
  }

  @AfterClass
  public static void shutdown(TestContext context) throws Exception {
    LOGGER.debug("performing shutdown");
    if (netRelay != null) {
      netRelay.stop();
    }
    netRelay = null;

    if (vertx != null) {
      Async async = context.async();
      vertx.close(ar -> {
        vertx = null;
        async.complete();
      });
      async.awaitSuccess();
    }
  }

  public KeyGeneratorVerticle createKeyGenerator(TestContext context) {
    KeyGeneratorSettings settings = new KeyGeneratorSettings();
    modifyKeyGeneratorVerticleSettings(context, settings);
    return new KeyGeneratorVerticle(settings);
  }

  /**
   * Possibility to adapt the settings to the needs of the test
   * 
   * @param context
   * @param settings
   */
  protected void modifyKeyGeneratorVerticleSettings(TestContext context, KeyGeneratorSettings settings) {
    settings.setKeyGeneratorClass(DebugGenerator.class);
  }

  protected static VertxOptions getVertxOptions() {
    return TestHelper.getOptions();
  }

  public void initNetRelay(TestContext context) {
    if (netRelay == null) {
      LOGGER.info("init NetRelay");
      netRelay = NetRelayExt_InternalSettings.getInstance(vertx, context, this);
    }
  }

  /**
   * This method is modifying the {@link Settings} which are used to init NetRelay. Here it defines the
   * name of the database
   * 
   * @param settings
   */
  public void modifySettings(TestContext context, Settings settings) {
    LOGGER.info("modifySettings, setting port to " + PORT);
    settings.setServerPort(PORT);
    settings.getDatastoreSettings().setDatabaseName(getClass().getSimpleName());
  }

  protected static final void testRequest(TestContext context, HttpMethod method, String path, int statusCode,
      String statusMessage) throws Exception {
    testRequest(context, method, path, null, statusCode, statusMessage, null);
  }

  protected static final void testRequest(TestContext context, HttpMethod method, String path, int statusCode,
      String statusMessage, String responseBody) throws Exception {
    testRequest(context, method, path, null, statusCode, statusMessage, responseBody);
  }

  protected static final void testRequest(TestContext context, HttpMethod method, String path, int statusCode,
      String statusMessage, Buffer responseBody) throws Exception {
    testRequestBuffer(context, method, path, null, null, statusCode, statusMessage, responseBody);
  }

  protected static final void testRequestWithContentType(TestContext context, HttpMethod method, String path,
      String contentType, int statusCode, String statusMessage) throws Exception {
    testRequest(context, method, path, req -> req.putHeader("content-type", contentType), statusCode, statusMessage,
        null);
  }

  protected static final void testRequestWithAccepts(TestContext context, HttpMethod method, String path,
      String accepts, int statusCode, String statusMessage) throws Exception {
    testRequest(context, method, path, req -> req.putHeader("accept", accepts), statusCode, statusMessage, null);
  }

  protected static final void testRequestWithCookies(TestContext context, HttpMethod method, String path,
      String cookieHeader, int statusCode, String statusMessage) throws Exception {
    testRequest(context, method, path, req -> req.putHeader("cookie", cookieHeader), statusCode, statusMessage, null);
  }

  protected static final void testRequest(TestContext context, HttpMethod method, String path,
      Consumer<HttpClientRequest> requestAction, int statusCode, String statusMessage, String responseBody)
      throws Exception {
    testRequest(context, method, path, requestAction, null, statusCode, statusMessage, responseBody);
  }

  protected static final void testRequest(TestContext context, HttpMethod method, String path,
      Consumer<HttpClientRequest> requestAction, Consumer<ResponseCopy> responseAction, int statusCode,
      String statusMessage, String responseBody) throws Exception {
    testRequestBuffer(context, method, path, requestAction, responseAction, statusCode, statusMessage,
        responseBody != null ? Buffer.buffer(responseBody) : null);
  }

  protected static final void testRequestBuffer(TestContext context, HttpMethod method, String path,
      Consumer<HttpClientRequest> requestAction, Consumer<ResponseCopy> responseAction, int statusCode,
      String statusMessage, Buffer responseBodyBuffer) throws Exception {
    testRequestBuffer(context, client, method, PORT, path, requestAction, responseAction, statusCode, statusMessage,
        responseBodyBuffer);
  }

  protected static final void testRequestBuffer(TestContext context, HttpClient client, HttpMethod method, int port,
      String path, Consumer<HttpClientRequest> requestAction, Consumer<ResponseCopy> responseAction, int statusCode,
      String statusMessage, Buffer responseBodyBuffer) throws Exception {
    LOGGER.info("calling URL " + path);
    Async async = context.async();
    ResultObject<ResponseCopy> resultObject = new ResultObject<>(null);

    Handler<Throwable> exceptionHandler = new Handler<Throwable>() {

      @Override
      public void handle(Throwable ex) {
        LOGGER.error("", ex);
        async.complete();
      }
    };

    HttpClientRequest req = client.request(method, port, HOSTNAME, path, resp -> {
      resp.exceptionHandler(exceptionHandler);

      ResponseCopy rc = new ResponseCopy();
      resp.bodyHandler(buff -> {
        rc.content = buff.toString();
        rc.code = resp.statusCode();
        rc.statusMessage = resp.statusMessage();
        rc.headers = MultiMap.caseInsensitiveMultiMap();
        rc.headers.addAll(resp.headers());
        rc.cookies = resp.cookies();
        resultObject.setResult(rc);
        async.complete();
      });
    });
    req.exceptionHandler(exceptionHandler);
    if (requestAction != null) {
      requestAction.accept(req);
    }
    req.end();
    async.await();

    ResponseCopy rc = resultObject.getResult();
    if (responseAction != null) {
      responseAction.accept(rc);
    }
    context.assertNotNull(rc, "Responsecopy is null");
    context.assertEquals(statusCode, rc.code);
    if (statusMessage != null)
      context.assertEquals(statusMessage, rc.statusMessage);
    if (responseBodyBuffer == null) {
      // async.complete();
    } else {
      context.assertEquals(responseBodyBuffer.toString(), rc.content);
    }
  }

  protected RouterDefinition defineRouterDefinition(Class controllerClass, String route) {
    RouterDefinition rd = new RouterDefinition();
    rd.setName(controllerClass.getSimpleName());
    rd.setControllerClass(controllerClass);
    rd.setRoutes(new String[] { route });
    return rd;
  }

  public static class ResponseCopy {
    public String content;
    public int code;
    public String statusMessage;
    public MultiMap headers;
    public List<String> cookies;
  }

  // "-DmailClientUserName=dev-test@braintags.net -DmailClientPassword=thoo4ati "
  protected void initMailClient(Settings settings) {
    String mailUserName = System.getProperty("mailClientUserName");
    if (mailUserName == null || mailUserName.hashCode() == 0) {
      throw new InitException("Need System parameter 'mailClientUserName'");
    }
    String mailClientPassword = System.getProperty("mailClientPassword");
    if (mailClientPassword == null || mailClientPassword.hashCode() == 0) {
      throw new InitException("Need System parameter 'mailClientPassword'");
    }
    MailClientSettings ms = settings.getMailClientSettings();
    ms.setHostname("mail.braintags.net");
    ms.setPort(8025);
    ms.setName("mailclient");
    ms.setUsername(mailUserName);
    ms.setPassword(mailClientPassword);
    ms.setSsl(false);
    ms.setStarttls(StartTLSOptions.DISABLED);
    ms.setActive(true);
  }
}

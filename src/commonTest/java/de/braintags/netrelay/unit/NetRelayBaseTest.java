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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.function.Consumer;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.impl.NetRelayExt_InternalSettings;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.routing.RouterDefinition;
import de.braintags.vertx.jomnigate.testdatastore.DatastoreBaseTest;
import de.braintags.vertx.jomnigate.testdatastore.TestHelper;
import de.braintags.vertx.keygenerator.KeyGeneratorSettings;
import de.braintags.vertx.keygenerator.KeyGeneratorVerticle;
import de.braintags.vertx.keygenerator.impl.DebugGenerator;
import de.braintags.vertx.util.ResultObject;
import de.braintags.vertx.util.exception.InitException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.mail.MailConfig;
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
public abstract class NetRelayBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(NetRelayBaseTest.class);

  /**
   * The name of the property, by which the port of NetRelay can be specified
   */
  public static final String SERVER_PORT_PROPERTY = "NetRelayPort";

  public static final String TESTS_MAIL_RECIPIENT = "mremme@braintags.de";
  public static final String TESTS_MAIL_FROM = "netrelayTesting@braintags.de";

  protected static Vertx vertx;
  private static HttpClient client;
  protected static NetRelay netRelay;
  protected KeyGeneratorVerticle keyGenVerticle;

  public static String HOSTNAME = "localhost";

  @Rule
  public TestRule rule = new DisableOnDebug(Timeout.seconds(getTimeout()));

  private static int getTimeout() {
    return Integer.parseInt(System.getProperty("testTimeout", "20"));
  }

  @Rule
  public TestName name = new TestName();

  @Before
  public void initBeforeTest(final TestContext context) {
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

  public void initTest(final TestContext context) {

  }

  protected void undeployVerticle(final TestContext context, final AbstractVerticle verticle) {
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
  public static void startup(final TestContext context) throws Exception {
    LOGGER.debug("starting class");
    vertx = Vertx.vertx(getVertxOptions());
    // boolean startMongoLocal = Boolean.getBoolean("startMongoLocal");
    // String portString = System.getProperty(MongoDataStoreInit.LOCAL_PORT_PROP, "27017");
    // int port = Integer.parseInt(portString);
  }

  @AfterClass
  public static void shutdown(final TestContext context) throws Exception {
    LOGGER.info("performing shutdown");
    Future<Void> stopFuture = Future.future();
    if (client != null) {
      client.close();
      client = null;
    }

    if (netRelay != null) {
      netRelay.stop(stopFuture);
    } else {
      stopFuture.complete();
    }

    Async async = context.async();
    stopFuture.setHandler(v -> {
      netRelay = null;
      if (v.failed()) {
        LOGGER.error("NetRelay did not stop", v.cause());
      } else {
        LOGGER.info("NetRelay stopped");
      }
      if (vertx != null) {
        vertx.close(ar -> {
          if (v.failed()) {
            LOGGER.error("vertx did not stop", v.cause());
          } else {
            LOGGER.info("vertx stopped");
          }
          vertx = null;
          async.complete();
        });
      }
    });
  }

  public KeyGeneratorVerticle createKeyGenerator(final TestContext context) {
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
  protected void modifyKeyGeneratorVerticleSettings(final TestContext context, final KeyGeneratorSettings settings) {
    settings.setKeyGeneratorClass(DebugGenerator.class);
  }

  protected static VertxOptions getVertxOptions() {
    return TestHelper.getOptions();
  }

  public void initNetRelay(final TestContext context) {
    if (netRelay == null) {
      LOGGER.info("init NetRelay");
      netRelay = NetRelayExt_InternalSettings.getInstance(vertx, context, this);
      LOGGER.info("NetRelay started on port " + getNetrelayPort());
    }
  }

  public static synchronized HttpClient getClient() {
    if (client == null) {
      client = vertx.createHttpClient(new HttpClientOptions().setDefaultPort(netRelay.getActualServerPort())
          .setConnectTimeout(getTimeout()).setIdleTimeout(getTimeout()));
    }
    return client;
  }

  protected static int getNetrelayPort() {
    return netRelay.getActualServerPort();
  }

  /**
   * This method is modifying the {@link Settings} which are used to init NetRelay. Here it defines the
   * name of the database
   * 
   * @param settings
   */
  public void modifySettings(final TestContext context, final Settings settings) {
    String portString = System.getProperty(SERVER_PORT_PROPERTY, null);
    if (portString != null) {
      settings.setServerPort(Integer.parseInt(portString));
    } else {
      settings.setServerPort(0);
    }
    LOGGER.info("modifySettings, setting port to " + settings.getServerPort());
    settings.getDatastoreSettings().setDatabaseName(getClass().getSimpleName());
  }

  protected static final void testRequest(final TestContext context, final HttpMethod method, final String path, final int statusCode,
      final String statusMessage) throws Exception {
    testRequest(context, method, path, null, statusCode, statusMessage, null);
  }

  protected static final void testRequest(final TestContext context, final HttpMethod method, final String path,
      final Consumer<HttpClientRequest> requestAction, final int statusCode, final String statusMessage, final String responseBody)
      throws Exception {
    testRequest(context, method, path, requestAction, null, statusCode, statusMessage, responseBody);
  }

  protected static final void testRequest(final TestContext context, final HttpMethod method, final String path,
      final Consumer<HttpClientRequest> requestAction, final Consumer<ResponseCopy> responseAction, final int statusCode,
      final String statusMessage, final String responseBody) throws Exception {
    testRequestBuffer(context, getClient(), method, getNetrelayPort(), path, requestAction, responseAction, statusCode,
        statusMessage,
        responseBody != null ? Buffer.buffer(responseBody) : null);
  }

  protected static final void testRequestBuffer(final TestContext context, final HttpClient client, final HttpMethod method, final int port,
      final String path, final Consumer<HttpClientRequest> requestAction, final Consumer<ResponseCopy> responseAction, final int statusCode,
      final String statusMessage, final Buffer responseBodyBuffer) throws Exception {
    LOGGER.info("calling URL " + path);
    Async async = context.async();
    ResultObject<ResponseCopy> resultObject = new ResultObject<>(null);

    Handler<Throwable> exceptionHandler = ex -> {
    LOGGER.error("", ex);
    async.complete();
    };

    HttpClientRequest req = client.request(method, port, HOSTNAME, path, resp -> {
      resp.exceptionHandler(exceptionHandler);

      ResponseCopy rc = new ResponseCopy();
      resp.bodyHandler(buff -> {
        LOGGER.debug("Executing body handler");
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
      try {
        requestAction.accept(req);
      } catch (Throwable t) {
        context.fail(t);
        return;
      }
    }
    req.end();
    async.await();

    LOGGER.debug("request executed");
    ResponseCopy rc = resultObject.getResult();
    if (responseAction != null) {
      try {
        responseAction.accept(rc);
      } catch (Throwable t) {
        context.fail(t);
        return;
      }
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

  protected RouterDefinition defineRouterDefinition(final Class controllerClass, final String route) {
    RouterDefinition rd = new RouterDefinition();
    rd.setName(controllerClass.getSimpleName());
    rd.setController(controllerClass);
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
  protected void initMailClient(final Settings settings) {
    String mailUserName = System.getProperty("mailClientUserName");
    if (mailUserName == null || mailUserName.hashCode() == 0) {
      throw new InitException("Need System parameter 'mailClientUserName'");
    }
    String mailClientPassword = System.getProperty("mailClientPassword");
    if (mailClientPassword == null || mailClientPassword.hashCode() == 0) {
      throw new InitException("Need System parameter 'mailClientPassword'");
    }
    MailConfig mailConfig = settings.getMailConfig();
    mailConfig.setHostname("mail.braintags.net");
    mailConfig.setPort(8025);
    mailConfig.setUsername(mailUserName);
    mailConfig.setPassword(mailClientPassword);
    mailConfig.setSsl(false);
    mailConfig.setStarttls(StartTLSOptions.DISABLED);
  }

  protected static <T> Handler<AsyncResult<T>> assertException(final Class<? extends Exception> exceptionClass,
      final TestContext context) {
    return context.asyncAssertFailure(failure -> {
      assertThat(failure, is(instanceOf(exceptionClass)));
    });
  }
}

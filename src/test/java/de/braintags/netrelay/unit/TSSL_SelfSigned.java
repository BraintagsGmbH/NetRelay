package de.braintags.netrelay.unit;

import org.junit.Test;

import de.braintags.netrelay.init.Settings;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;

/**
 * Test ssl server
 * 
 * @author Michael Remme
 * 
 */
public class TSSL_SelfSigned extends NetRelayBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TSSL_SelfSigned.class);
  public static final int SSL_PORT = 8090;

  @Test
  public void testSimpleRequests(TestContext context) throws Exception {

    try {
      resetRoutes();
      String url = "/";
      testRequest(context, HttpMethod.GET, url, req -> {
      }, resp -> {
        context.assertNotNull(resp, "response is null");
        LOGGER.info("RESPONSE: " + resp.content);
        LOGGER.info("HEADERS: " + resp.headers);
      }, 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }

  }

  /**
   * @throws Exception
   */
  private void resetRoutes() throws Exception {
    netRelay.resetRoutes();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelayBaseTest#modifySettings(de.braintags.netrelay.init.Settings)
   */
  @Override
  public void modifySettings(TestContext context, Settings settings) {
    super.modifySettings(context, settings);
    settings.setSslPort(SSL_PORT);
    settings.setCertificatePassword("gundelei");
    settings.setCertificateSelfSigned(true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.unit.NetRelayBaseTest#initBeforeTest(io.vertx.ext.unit.TestContext)
   */
  @Override
  public void initTest(TestContext context) {
    HttpClientOptions options = new HttpClientOptions();
    options.setSsl(true);
    options.setDefaultPort(SSL_PORT);
    options.setTrustAll(true);
    options.setVerifyHost(false);
    client = vertx.createHttpClient(options);
  }

}

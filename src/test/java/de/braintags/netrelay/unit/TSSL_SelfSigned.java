package de.braintags.netrelay.unit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Test;

import de.braintags.netrelay.init.Settings;
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
  public static final int SSL_PORT = 8091;

  static {
    // Create a trust manager that does not validate certificate chains
    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
      @Override
      public X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      @Override
      public void checkClientTrusted(X509Certificate[] certs, String authType) {
      }

      @Override
      public void checkServerTrusted(X509Certificate[] certs, String authType) {
      }
    } };

    // Install the all-trusting trust manager
    try {
      SSLContext sc = SSLContext.getInstance("TLS");
      sc.init(null, trustAllCerts, new SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    } catch (Exception e) {
      LOGGER.warn("", e);
    }

  }

  @Test
  public void test(TestContext context) throws Exception {
    String httpsURL = "https://localhost:" + SSL_PORT + "/static/index.html";
    URL myurl = new URL(httpsURL);
    HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
    con.setHostnameVerifier(new HostnameVerifier() {

      @Override
      public boolean verify(String hostname, SSLSession session) {
        return true;
      }
    });

    InputStream ins = con.getInputStream();
    InputStreamReader isr = new InputStreamReader(ins);
    BufferedReader in = new BufferedReader(isr);
    StringBuilder sb = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      sb.append(inputLine);
    }
    in.close();
    context.assertTrue(sb.toString().contains("testcontent"), "Did not find expected message");
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
    settings.setServerPort(8888);
    settings.setSslPort(SSL_PORT);
    settings.setCertificatePassword("gundelei");
    settings.setCertificateSelfSigned(true);
  }

  // public void initTest(TestContext context) {
  // HttpClientOptions options = new HttpClientOptions();
  // options.setSsl(true);
  // options.setDefaultPort(SSL_PORT);
  // options.setTrustAll(true);
  // options.setVerifyHost(false);
  // options.setKeepAlive(true);
  // options.setTcpKeepAlive(true);
  // client = vertx.createHttpClient(options);
  // }

}

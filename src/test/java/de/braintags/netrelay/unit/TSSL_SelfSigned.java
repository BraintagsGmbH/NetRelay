/*-
 * #%L
 * netrelay
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
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

import org.junit.Ignore;
import org.junit.Test;

import de.braintags.netrelay.init.Settings;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

/**
 * Test ssl server
 * 
 * @author Michael Remme
 * 
 */
@Ignore("Self signed will be implemented by vertx shortly")
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

  /**
   * Test can be deactivated, if needed.
   * Strange behaviour: when using the default HttpClient from the super class, it is crashing, cause certificate can't
   * be read.
   * With this it works fine
   * 
   * @param context
   */
  @Test
  public void testC(TestContext context) {
    Async async = context.async();
    HttpClientOptions options = new HttpClientOptions();
    options.setSsl(true);
    options.setDefaultPort(SSL_PORT);
    options.setTrustAll(true);
    options.setVerifyHost(false);
    options.setKeepAlive(true);
    options.setTcpKeepAlive(true);

    Handler<Throwable> exceptionHandler = new Handler<Throwable>() {

      @Override
      public void handle(Throwable ex) {
        LOGGER.error("", ex);
        async.complete();
      }
    };

    HttpClient client = vertx.createHttpClient(options);

    HttpClientRequest req = client.request(HttpMethod.GET, SSL_PORT, HOSTNAME, "/", resp -> {
      resp.exceptionHandler(exceptionHandler);

      resp.bodyHandler(buff -> {
        LOGGER.info(buff.toString());
        LOGGER.info(resp.statusCode());
        LOGGER.info(resp.statusMessage());
        LOGGER.info(resp.headers());
        LOGGER.info(resp.cookies());
        async.complete();
      });

    });
    req.exceptionHandler(exceptionHandler);
    req.end();

    async.await();
    LOGGER.info("finished");
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

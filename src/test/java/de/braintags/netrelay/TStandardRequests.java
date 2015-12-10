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

import org.junit.Test;

import de.braintags.netrelay.controller.StandarRequestController;
import de.braintags.netrelay.controller.impl.ThymeleafTemplateController;
import de.braintags.netrelay.init.Settings;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;

/**
 * Test the TemplateController of NetRelay
 * 
 * @author Michael Remme
 * 
 */
public class TStandardRequests extends NetRelayBaseTest {

  @Test
  public void testFormURLEncoded(TestContext context) throws Exception {
    StandarRequestController.controllerProcessed = false;
    StandarRequestController.attrs = null;
    StandarRequestController.params = null;

    netRelay.getSettings().getRouterDefinitions().addBefore(ThymeleafTemplateController.class.getSimpleName(),
        StandarRequestController.createRouterDefinition());
    netRelay.resetRoutes();

    testRequest(context, HttpMethod.POST, "/test.html", req -> {
      Buffer buffer = Buffer.buffer();
      buffer.appendString("origin=junit-testUserAlias&login=admin%40foo.bar&pass+word=admin");
      req.headers().set("content-length", String.valueOf(buffer.length()));
      req.headers().set("content-type", "application/x-www-form-urlencoded");
      req.write(buffer);
    } , 200, "OK", null);
    context.assertTrue(StandarRequestController.controllerProcessed, "handler wasn't executed");

    context.assertNotNull(StandarRequestController.attrs);
    context.assertEquals(3, StandarRequestController.attrs.size());
    context.assertEquals("junit-testUserAlias", StandarRequestController.attrs.get("origin"));
    context.assertEquals("admin@foo.bar", StandarRequestController.attrs.get("login"));
    context.assertEquals("admin", StandarRequestController.attrs.get("pass word"));
  }

  @Test
  public void testFormMultipartFormDataNoMergeAttributes(TestContext context) throws Exception {
    testFormMultipartFormData(context, true);
  }

  private void testFormMultipartFormData(TestContext context, boolean mergeAttributes) throws Exception {
    StandarRequestController.controllerProcessed = false;
    StandarRequestController.attrs = null;
    StandarRequestController.params = null;

    netRelay.getSettings().getRouterDefinitions().addBefore(ThymeleafTemplateController.class.getSimpleName(),
        StandarRequestController.createRouterDefinition());
    netRelay.resetRoutes();

    testRequest(context, HttpMethod.POST, "/test.html?p1=foo", req -> {
      String boundary = "dLV9Wyq26L_-JQxk6ferf-RT153LhOO";
      Buffer buffer = Buffer.buffer();
      String str = "--" + boundary + "\r\n" + "Content-Disposition: form-data; name=\"attr1\"\r\n\r\nTim\r\n" + "--"
          + boundary + "\r\n" + "Content-Disposition: form-data; name=\"attr2\"\r\n\r\nJulien\r\n" + "--" + boundary
          + "--\r\n";
      buffer.appendString(str);
      req.headers().set("content-length", String.valueOf(buffer.length()));
      req.headers().set("content-type", "multipart/form-data; boundary=" + boundary);
      req.write(buffer);
    } , 200, "OK", null);
    context.assertTrue(StandarRequestController.controllerProcessed, "handler wasn't executed");

    MultiMap attrs = StandarRequestController.attrs;
    context.assertNotNull(attrs);
    context.assertEquals(2, attrs.size());
    context.assertEquals("Tim", attrs.get("attr1"));
    context.assertEquals("Julien", attrs.get("attr2"));
    MultiMap params = StandarRequestController.params;

    if (mergeAttributes) {
      context.assertNotNull(params);
      context.assertEquals(3, params.size());
      context.assertEquals("Tim", params.get("attr1"));
      context.assertEquals("Julien", params.get("attr2"));
      context.assertEquals("foo", params.get("p1"));
    } else {
      context.assertNotNull(params);
      context.assertEquals(1, params.size());
      context.assertEquals("foo", params.get("p1"));
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelayBaseTest#initTest()
   */
  @Override
  public void initTest() {
    super.initTest();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelayBaseTest#modifySettings(de.braintags.netrelay.init.Settings)
   */
  @Override
  protected void modifySettings(TestContext context, Settings settings) {
    super.modifySettings(context, settings);
  }

}

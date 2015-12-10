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

import de.braintags.netrelay.controller.impl.BodyController;
import de.braintags.netrelay.controller.impl.persistence.PersistenceController;
import de.braintags.netrelay.impl.NetRelayExt_FileBasedSettings;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TPersistenceController extends AbstractCaptureParameterTest {

  @Test
  public void testInsert(TestContext context) throws Exception {
    try {
      String url = String.format("/products/%s/INSERT/detail.html", NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME);
      testRequest(context, HttpMethod.POST, url, req -> {
        Buffer buffer = Buffer.buffer();
        buffer.appendString("origin=junit-testUserAlias&login=admin%40foo.bar&pass+word=admin");
        buffer.appendString("&").appendString(NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME)
            .appendString(".name=myFirstName");
        buffer.appendString("&").appendString(NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME).appendString(".age=18");
        buffer.appendString("&").appendString(NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME)
            .appendString(".child=true");
        req.headers().set("content-length", String.valueOf(buffer.length()));
        req.headers().set("content-type", "application/x-www-form-urlencoded");
        req.write(buffer);
      } , 200, "OK", null);

      // testRequest(context, HttpMethod.GET, "/products/nase/INSERT/detail.html", 200, "OK");
      //
      // testRequest(HttpMethod.POST, "/", rq -> {
      // Buffer buffer = Buffer.buffer();
      // buffer.appendString("origin=junit-testUserAlias&login=admin%40foo.bar&pass+word=admin");
      // rq.headers().set("content-length", String.valueOf(buffer.length()));
      // rq.headers().set("content-type", "application/x-www-form-urlencoded");
      // rq.write(buffer);
      // } , 200, "OK", null);
      //
      // testRequest(HttpMethod.POST, "/?p1=foo", req -> {
      // String boundary = "dLV9Wyq26L_-JQxk6ferf-RT153LhOO";
      // Buffer buffer = Buffer.buffer();
      // String str = "--" + boundary + "\r\n" + "Content-Disposition: form-data; name=\"attr1\"\r\n\r\nTim\r\n" + "--"
      // + boundary + "\r\n" + "Content-Disposition: form-data; name=\"attr2\"\r\n\r\nJulien\r\n" + "--" + boundary
      // + "--\r\n";
      // buffer.appendString(str);
      // req.headers().set("content-length", String.valueOf(buffer.length()));
      // req.headers().set("content-type", "multipart/form-data; boundary=" + boundary);
      // req.write(buffer);
      // } , 200, "OK", null);
      //
      // assertValues(context, 0, "nase", "tuEs", "12");
      // context.assertEquals("/products/detail.html", CaptureTestController.cleanedPath);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelayBaseTest#modifySettings(de.braintags.netrelay.init.Settings)
   */
  @Override
  protected void modifySettings(TestContext context, Settings settings) {
    super.modifySettings(context, settings);
    RouterDefinition def = settings.getRouterDefinitions().remove(PersistenceController.class.getSimpleName());
    def.setRoutes(new String[] { "/products/:entity/:action/detail.html" });
    settings.getRouterDefinitions().addAfter(BodyController.class.getSimpleName(), def);
  }

}

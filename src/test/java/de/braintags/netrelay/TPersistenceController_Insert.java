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
public class TPersistenceController_Insert extends AbstractPersistenceControllerTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TPersistenceController_Insert.class);

  @Test
  public void testInsertAsCapture(TestContext context) throws Exception {
    try {
      String url = String.format("/products/%s/INSERT/insert.html", NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME);
      Buffer responseBuffer = Buffer.buffer();
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
      } , resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        context.assertTrue(resp.content.toString().contains("myFirstName"), "Expected name not found");
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  @Test
  public void testInsertAsParameter(TestContext context) throws Exception {
    try {
      String url = String.format("/products/insert2.html?action=INSERT&entity=%s",
          NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME);
      Buffer responseBuffer = Buffer.buffer();
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
      } , resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        context.assertTrue(resp.content.toString().contains("myFirstName"), "Expected name not found in response");
      } , 200, "OK", null);
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
    def.setRoutes(new String[] { "/products/:entity/:action/insert.html", "/products/insert2.html" });
    settings.getRouterDefinitions().addAfter(BodyController.class.getSimpleName(), def);
  }

}

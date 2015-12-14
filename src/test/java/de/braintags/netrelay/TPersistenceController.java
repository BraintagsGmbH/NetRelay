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

import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.util.ResultObject;
import de.braintags.netrelay.controller.impl.BodyController;
import de.braintags.netrelay.controller.impl.persistence.PersistenceController;
import de.braintags.netrelay.impl.NetRelayExt_FileBasedSettings;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.mapper.SimpleNetRelayMapper;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TPersistenceController extends AbstractCaptureParameterTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TPersistenceController.class);

  // @Test
  public void testInsert(TestContext context) throws Exception {
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
        resp.bodyHandler(buff -> {
          LOGGER.info("RESPONSE: " + buff);
          context.assertTrue(buff.toString().contains("myFirstName"), "Expected name not found");
        });
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  @Test
  public void testDisplay(TestContext context) {
    Async async = context.async();
    IWrite<SimpleNetRelayMapper> write = netRelay.getDatastore().createWrite(SimpleNetRelayMapper.class);
    SimpleNetRelayMapper mapper = new SimpleNetRelayMapper();
    mapper.age = 13;
    mapper.child = false;
    mapper.name = "testmapper for display";
    write.add(mapper);
    ResultObject<SimpleNetRelayMapper> ro = new ResultObject<>(null);
    write.save(result -> {
      if (result.failed()) {
        context.fail(result.cause());
        async.complete();
      } else {
        // all fine, ID should be set in mapper
        LOGGER.info("ID: " + mapper.id);
        async.complete();
      }
    });
    async.await();

    try {
      String id = mapper.id;
      String url = String.format("/products/%s/DISPLAY/%s/detail.html", NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME,
          id);
      testRequest(context, HttpMethod.POST, url, null, resp -> {
        resp.bodyHandler(buff -> {
          LOGGER.info("RESPONSE: " + buff);
          context.assertTrue(buff.toString().contains("testmapper for display"), "Expected name not found");
        });
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
    def.setRoutes(
        new String[] { "/products/:entity/:action/insert.html", "/products/:entity/:action/:ID/detail.html" });

    settings.getRouterDefinitions().addAfter(BodyController.class.getSimpleName(), def);
  }

}

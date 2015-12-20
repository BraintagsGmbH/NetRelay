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

import de.braintags.io.vertx.pojomapper.testdatastore.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.testdatastore.ResultContainer;
import de.braintags.netrelay.controller.impl.BodyController;
import de.braintags.netrelay.controller.impl.persistence.PersistenceController;
import de.braintags.netrelay.impl.NetRelayExt_FileBasedSettings;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.mapper.SimpleNetRelayMapper;
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
public class TPersistenceController_Update extends AbstractPersistenceControllerTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TPersistenceController_Update.class);

  @Test
  public void testUpdate(TestContext context) {
    SimpleNetRelayMapper mapper = new SimpleNetRelayMapper();
    mapper.age = 13;
    mapper.child = true;
    mapper.name = "testmapper for update";
    ResultContainer rc = DatastoreBaseTest.saveRecord(context, mapper);
    if (rc.assertionError != null)
      throw rc.assertionError;

    Object id = rc.writeResult.iterator().next().getId();
    LOGGER.info("ID: " + id);

    try {
      String url = String.format("/products/%s/UPDATE/%s/update.html", NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME,
          id);

      testRequest(context, HttpMethod.POST, url, req -> {
        Buffer buffer = Buffer.buffer();
        buffer.appendString("origin=junit-testUserAlias&login=admin%40foo.bar&pass+word=admin");
        buffer.appendString("&").appendString(NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME)
            .appendString(".name=updatePerformed");
        buffer.appendString("&").appendString(NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME).appendString(".age=20");
        req.headers().set("content-length", String.valueOf(buffer.length()));
        req.headers().set("content-type", "application/x-www-form-urlencoded");
        req.write(buffer);
      } , resp -> {
        context.assertNotNull(resp);
        String content = resp.content;
        LOGGER.info("RESPONSE: " + content);
        context.assertTrue(content.contains("updatePerformed"), "Update was not performed");
        context.assertTrue(content.contains("20"), "updated age was not saved");
        // child was not modified in request and should stay true
        context.assertTrue(content.contains("true"), "property child was modified, but should not");
      } , 200, "OK", null);

    } catch (Exception e) {
      context.fail(e);
    }

  }

  @Test
  public void testUpdateAsParameter(TestContext context) {
    SimpleNetRelayMapper mapper = new SimpleNetRelayMapper();
    mapper.age = 13;
    mapper.child = true;
    mapper.name = "testmapper for update";
    ResultContainer rc = DatastoreBaseTest.saveRecord(context, mapper);
    if (rc.assertionError != null)
      throw rc.assertionError;

    Object id = rc.writeResult.iterator().next().getId();
    LOGGER.info("ID: " + id);

    try {

      String url = "/products/update2.html?action=UPDATE&entity=" + NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME
          + "&ID=" + id;
      testRequest(context, HttpMethod.POST, url, req -> {
        Buffer buffer = Buffer.buffer();
        buffer.appendString("origin=junit-testUserAlias&login=admin%40foo.bar&pass+word=admin");
        buffer.appendString("&").appendString(NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME)
            .appendString(".name=updatePerformed");
        buffer.appendString("&").appendString(NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME).appendString(".age=20");
        req.headers().set("content-length", String.valueOf(buffer.length()));
        req.headers().set("content-type", "application/x-www-form-urlencoded");
        req.write(buffer);
      } , resp -> {
        context.assertNotNull(resp);
        String content = resp.content;
        LOGGER.info("RESPONSE: " + content);
        context.assertTrue(content.contains("updatePerformed"), "Update was not performed");
        context.assertTrue(content.contains("20"), "updated age was not saved");
        // child was not modified in request and should stay true
        context.assertTrue(content.contains("true"), "property child was modified, but should not");
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
    def.setRoutes(new String[] { "/products/:entity/:action/:ID/update.html", "/products/update2.html" });

    settings.getRouterDefinitions().addAfter(BodyController.class.getSimpleName(), def);
  }

}

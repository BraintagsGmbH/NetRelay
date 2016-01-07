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
import de.braintags.netrelay.controller.impl.BodyController;
import de.braintags.netrelay.controller.impl.persistence.PersistenceController;
import de.braintags.netrelay.impl.NetRelayExt_FileBasedSettings;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.mapper.SimpleNetRelayMapper;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TPersistenceController_Display extends AbstractPersistenceControllerTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TPersistenceController_Display.class);

  @Test
  public void testDisplaySingleRecord(TestContext context) {
    SimpleNetRelayMapper mapper = new SimpleNetRelayMapper();
    mapper.age = 13;
    mapper.child = false;
    mapper.name = "testmapper for display";
    DatastoreBaseTest.saveRecord(context, mapper);

    try {
      String id = mapper.id;
      String url = String.format("/products/%s/DISPLAY/%s/detail.html", NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME,
          id);
      testRequest(context, HttpMethod.POST, url, null, resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        context.assertTrue(resp.content.toString().contains("testmapper for display"), "Expected name not found");
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }

  }

  @Test
  public void testDisplaySingleRecordAsParam(TestContext context) {
    SimpleNetRelayMapper mapper = new SimpleNetRelayMapper();
    mapper.age = 13;
    mapper.child = false;
    mapper.name = "testmapper for display";
    DatastoreBaseTest.saveRecord(context, mapper);

    try {
      String id = mapper.id;
      String url = "/products/detail2.html?action=DISPLAY&entity=" + NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME
          + "&ID=" + id;
      testRequest(context, HttpMethod.POST, url, null, resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        context.assertTrue(resp.content.toString().contains("testmapper for display"), "Expected name not found");
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  @Test
  public void testDisplayListAll(TestContext context) {
    try {
      SimpleNetRelayMapper mapper = new SimpleNetRelayMapper();
      mapper.age = 13;
      mapper.child = false;
      mapper.name = "testmapper for display";
      DatastoreBaseTest.saveRecord(context, mapper);

      String url = String.format("/products/%s/DISPLAY/list.html", NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME);
      testRequest(context, HttpMethod.POST, url, null, resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        context.assertTrue(resp.content.toString().contains("success"), "Expected name not found");
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  @Test
  public void testDisplayListAllAsParam(TestContext context) {
    try {
      SimpleNetRelayMapper mapper = new SimpleNetRelayMapper();
      mapper.age = 13;
      mapper.child = false;
      mapper.name = "testmapper for display";
      DatastoreBaseTest.saveRecord(context, mapper);

      String url = "/products/list.html?action=DISPLAY&entity=" + NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME;
      testRequest(context, HttpMethod.POST, url, null, resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        context.assertTrue(resp.content.toString().contains("success"), "Expected name not found");
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  @Test
  public void testDisplayList_MaxRecords(TestContext context) {
    context.fail(new UnsupportedOperationException());
  }

  @Test
  public void testDisplayList_Pagination(TestContext context) {
    context.fail(new UnsupportedOperationException());
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
    def.setRoutes(new String[] { "/products/:entity/:action/list.html", "/products/:entity/:action/:ID/detail.html",
        "/products/:entity/:action/list2.html", "/products/detail2.html" });

    settings.getRouterDefinitions().addAfter(BodyController.class.getSimpleName(), def);
  }

}

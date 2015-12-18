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

import de.braintags.netrelay.controller.impl.api.DataTablesController;
import de.braintags.netrelay.init.Settings;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;

/**
 * Testing of {@link DataTablesController}
 * 
 * @author Michael Remme
 * 
 */
public class TDataTablesController extends NetRelayBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TDataTablesController.class);

  public static final Buffer LINK = Buffer.buffer("http://localhost:8080/api/datatable?mapper=Member&")
      .appendString("sEcho=76&iColumns=7&sColumns=id%2Cusername%2Cfirstname%2Clastname%2Cemail%2C%2Cid&")
      .appendString("iDisplayStart=0&iDisplayLength=10&")
      .appendString("mDataProp_0=0&sSearch_0=11&bRegex_0=false&bSearchable_0=true&bSortable_0=true&")
      .appendString("mDataProp_1=1&sSearch_1=michael.remme&bRegex_1=false&bSearchable_1=true&bSortable_1=true&")
      .appendString("mDataProp_2=2&sSearch_2=michael&bRegex_2=false&bSearchable_2=true&bSortable_2=true&")
      .appendString("mDataProp_3=3&sSearch_3=remme&bRegex_3=false&bSearchable_3=true&bSortable_3=true&")
      .appendString(
          "mDataProp_4=4&sSearch_4=m.remme%40braintags.de&bRegex_4=false&bSearchable_4=true&bSortable_4=true&")
      .appendString("mDataProp_5=5&sSearch_5=&bRegex_5=false&bSearchable_5=false&bSortable_5=false&")
      .appendString("mDataProp_6=6&sSearch_6=&bRegex_6=false&bSearchable_6=false&bSortable_6=false&").appendString(
          "sSearch=&bRegex=false&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&sRangeSeparator=~&more_data=my_value");

  @Test
  public void testParameters1(TestContext context) throws Exception {
    try {
      String url = LINK.toString();
      testRequest(context, HttpMethod.GET, url, null, resp -> {
        String response = resp.content.toString();
        LOGGER.info("RESPONSE: " + response);
        JsonObject json = new JsonObject(response);
        checkKey(context, json, "iTotalRecords");
        checkKey(context, json, "iTotalDisplayRecords");

      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }

  }

  private void checkKey(TestContext context, JsonObject json, String key) {
    context.assertTrue(json.containsKey(key), "key does not exist in reply: " + key);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelayBaseTest#modifySettings(de.braintags.netrelay.init.Settings)
   */
  @Override
  protected void modifySettings(TestContext context, Settings settings) {
    super.modifySettings(context, settings);
    // defineRouterDefinitions adds the default key-definitions
    settings.getRouterDefinitions().add(0, defineRouterDefinition(DataTablesController.class, "/api/datatable"));
  }

}

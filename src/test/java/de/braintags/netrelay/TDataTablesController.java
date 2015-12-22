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
import de.braintags.netrelay.controller.impl.api.DataTablesController;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.model.Member;
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
      .appendString("sEcho=76&iColumns=7&sColumns=id%2CuserName%2CfirstName%2ClastName%2Cemail%2C%2Cid&")
      .appendString("iDisplayStart=0&iDisplayLength=10&")
      .appendString("mDataProp_0=0&sSearch_0=&bRegex_0=false&bSearchable_0=true&bSortable_0=true&")
      .appendString("mDataProp_1=1&sSearch_1=&bRegex_1=false&bSearchable_1=true&bSortable_1=true&")
      .appendString("mDataProp_2=2&sSearch_2=&bRegex_2=false&bSearchable_2=true&bSortable_2=true&")
      .appendString("mDataProp_3=3&sSearch_3=&bRegex_3=false&bSearchable_3=true&bSortable_3=true&")
      .appendString("mDataProp_4=4&sSearch_4=&bRegex_4=false&bSearchable_4=true&bSortable_4=true&")
      .appendString("mDataProp_5=5&sSearch_5=&bRegex_5=false&bSearchable_5=false&bSortable_5=false&")
      .appendString("mDataProp_6=6&sSearch_6=&bRegex_6=false&bSearchable_6=false&bSortable_6=false&").appendString(
          "sSearch=&bRegex=false&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&sRangeSeparator=~&more_data=my_value");

  public static final Buffer LINK2 = Buffer.buffer("http://localhost:8080/api/datatable?mapper=Member&")
      .appendString("sEcho=76&iColumns=7&sColumns=id%2CuserName%2CfirstName%2ClastName%2Cemail%2C%2Cid&")
      .appendString("iDisplayStart=0&iDisplayLength=10&")
      .appendString("mDataProp_0=0&sSearch_0=&bRegex_0=false&bSearchable_0=true&bSortable_0=true&")
      .appendString("mDataProp_1=1&sSearch_1=remme&bRegex_1=false&bSearchable_1=true&bSortable_1=true&")
      .appendString("mDataProp_2=2&sSearch_2=&bRegex_2=false&bSearchable_2=true&bSortable_2=true&")
      .appendString("mDataProp_3=3&sSearch_3=&bRegex_3=false&bSearchable_3=true&bSortable_3=true&")
      .appendString("mDataProp_4=4&sSearch_4=&bRegex_4=false&bSearchable_4=true&bSortable_4=true&")
      .appendString("mDataProp_5=5&sSearch_5=&bRegex_5=false&bSearchable_5=false&bSortable_5=false&")
      .appendString("mDataProp_6=6&sSearch_6=&bRegex_6=false&bSearchable_6=false&bSortable_6=false&").appendString(
          "sSearch=&bRegex=false&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&sRangeSeparator=~&more_data=my_value");

  public static final Buffer LINK3 = Buffer.buffer("http://localhost:8080/api/datatable?mapper=Member&")
      .appendString("sEcho=76&iColumns=7&sColumns=id%2CuserName%2CfirstName%2ClastName%2Cemail%2C%2Cid&")
      .appendString("iDisplayStart=0&iDisplayLength=2&")
      .appendString("mDataProp_0=0&sSearch_0=&bRegex_0=false&bSearchable_0=true&bSortable_0=true&")
      .appendString("mDataProp_1=1&sSearch_1=&bRegex_1=false&bSearchable_1=true&bSortable_1=true&")
      .appendString("mDataProp_2=2&sSearch_2=&bRegex_2=false&bSearchable_2=true&bSortable_2=true&")
      .appendString("mDataProp_3=3&sSearch_3=&bRegex_3=false&bSearchable_3=true&bSortable_3=true&")
      .appendString("mDataProp_4=4&sSearch_4=&bRegex_4=false&bSearchable_4=true&bSortable_4=true&")
      .appendString("mDataProp_5=5&sSearch_5=&bRegex_5=false&bSearchable_5=false&bSortable_5=false&")
      .appendString("mDataProp_6=6&sSearch_6=&bRegex_6=false&bSearchable_6=false&bSortable_6=false&").appendString(
          "sSearch=&bRegex=false&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&sRangeSeparator=~&more_data=my_value");

  public static final Buffer TMP_LINK = Buffer.buffer("http://localhost:8080/api/datatable?")
      .appendString("sEcho=1&iColumns=6&sColumns=id%2CuserName%2CfirstName%2ClastName%2Cemail%2Cid")
      .appendString(
          "&iDisplayStart=0&iDisplayLength=10&mDataProp_0=0&sSearch_0=&bRegex_0=false&bSearchable_0=true&bSortable_0=true")
      .appendString("&mDataProp_1=1&sSearch_1=&bRegex_1=false&bSearchable_1=true&bSortable_1=true")
      .appendString("&mDataProp_2=2&sSearch_2=&bRegex_2=false&bSearchable_2=true&bSortable_2=true")
      .appendString("&mDataProp_3=3&sSearch_3=&bRegex_3=false&bSearchable_3=true&bSortable_3=true")
      .appendString("&mDataProp_4=4&sSearch_4=&bRegex_4=false&bSearchable_4=true&bSortable_4=true")
      .appendString("&mDataProp_5=5&sSearch_5=&bRegex_5=false&bSearchable_5=false&bSortable_5=false")
      .appendString("&sSearch=&bRegex=false&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&sRangeSeparator=~")
      .appendString("&mapper=Member");

  //

  @Test
  public void testObjectWithNullField(TestContext context) throws Exception {
    // Mapper mit Field erzeugen, das NULL als value hat; einmal Feld als String, einmal als Object
    throw new UnsupportedOperationException();
  }

  @Test
  public void testAllRecords(TestContext context) throws Exception {
    String url = LINK.toString();
    testParameters1(context, url, 3, 3);
  }

  @Test
  public void testSearchUsername(TestContext context) throws Exception {
    String url = LINK2.toString();
    testParameters1(context, url, 1, 1);
  }

  @Test
  public void testLimitRecords(TestContext context) throws Exception {
    String url = LINK3.toString();
    testParameters1(context, url, 3, 2);
  }

  public void testParameters1(TestContext context, String link, int total, int selection) throws Exception {
    try {
      testRequest(context, HttpMethod.GET, link, null, resp -> {
        String response = resp.content.toString();
        LOGGER.info("RESPONSE: " + response);
        JsonObject json = new JsonObject(response);
        checkKey(context, json, "iTotalRecords");
        checkKey(context, json, "iTotalDisplayRecords");
        context.assertEquals(total, json.getInteger("iTotalRecords"));
        context.assertEquals(selection, json.getInteger("iTotalDisplayRecords"));
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  @Override
  public void initTest(TestContext context) {
    DatastoreBaseTest.EXTERNAL_DATASTORE = netRelay.getDatastore();
    DatastoreBaseTest.clearTable(context, Member.class);
    DatastoreBaseTest.saveRecord(context,
        initMember("Herr", "Michael", "Remme", "mremme@braintags.de", "mremme@braintags.de"));
    DatastoreBaseTest.saveRecord(context,
        initMember("Herr", "Waldemar", "Safenreider", "wsafenreider@braintags.de", "wsafenreider@braintags.de"));
    DatastoreBaseTest.saveRecord(context,
        initMember("Frau", "Waltraud", "Kunigunde", "waltraud@kunigunde.de", "waltraud@kunigunde.de"));
  }

  private Member initMember(String gender, String firstName, String lastName, String email, String userName) {
    Member member = new Member();
    member.setEmail(email);
    member.setFirstName(firstName);
    member.setGender(gender);
    member.setLastName(lastName);
    member.setUserName(userName);
    return member;
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
    settings.getMappingDefinitions().addMapperDefinition("Member", Member.class);
  }

}

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
import de.braintags.netrelay.util.MultipartUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.test.core.TestUtils;

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
  public void testInsertAsParameterWithFile(TestContext context) throws Exception {
    try {
      String url = String.format("/products/insert3.html?action=INSERT&entity=%s",
          NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME);
      MultipartUtil mu = new MultipartUtil();
      addFields(mu);

      String uploadsDir = BodyHandler.DEFAULT_UPLOADS_DIRECTORY;
      String fieldName = NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME + ".fileName";
      String fileName = "somefile.dat";
      String contentType = "application/octet-stream";
      Buffer fileData = TestUtils.randomBuffer(50);
      mu.addFilePart(fieldName, fileName, contentType, fileData);

      testRequest(context, HttpMethod.POST, url, req -> {
        mu.finish(req);
      } , resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        String response = resp.content.toString();
        context.assertTrue(response.contains("myFirstName"), "Expected name not found in response");
        context.assertTrue(response.contains("somefile.dat"), "Expected filename not fount in response");
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  @Test
  public void testInsertAsCapture(TestContext context) throws Exception {
    try {
      String url = String.format("/products/%s/INSERT/insert.html", NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME);
      MultipartUtil mu = new MultipartUtil();
      addFields(mu);
      testRequest(context, HttpMethod.POST, url, req -> {
        mu.finish(req);
      } , resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        context.assertTrue(resp.content.toString().contains("myFirstName"), "Expected name not found");
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * @param mu
   */
  protected void addFields(MultipartUtil mu) {
    mu.addFormField("origin", "junit-testUserAlias");
    mu.addFormField("login", "admin@foo.bar");
    mu.addFormField("pass word", "admin");
    mu.addFormField(NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME + ".name", "myFirstName");
    mu.addFormField(NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME + ".age", "18");
    mu.addFormField(NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME + ".child", "true");
  }

  @Test
  public void testInsertAsParameter(TestContext context) throws Exception {
    try {
      String url = String.format("/products/insert2.html?action=INSERT&entity=%s",
          NetRelayExt_FileBasedSettings.SIMPLEMAPPER_NAME);
      MultipartUtil mu = new MultipartUtil();
      addFields(mu);
      testRequest(context, HttpMethod.POST, url, req -> {
        mu.finish(req);
      } , resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        context.assertTrue(resp.content.toString().contains("myFirstName"), "Expected name not found in response");
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /*
   * <td>Bild <br>
   * <input class="inputField" name="logo" style="width: 100%" type="file">
   * <input name="bt_PayType$$logo" style="width:100%" id="logo" type="hidden">
   * <a target="_blank" href="/datafiles/"><img src="/datafiles/administration/iconLupe.gif" alt="Vorschau"
   * title="Vorschau" border="0"></a>
   * </td>
   * 
   */

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
        new String[] { "/products/:entity/:action/insert.html", "/products/insert2.html", "/products/insert3.html" });
    def.getHandlerProperties().put(PersistenceController.UPLOAD_DIRECTORY_PROP, "webroot/images/productImages");

    settings.getRouterDefinitions().addAfter(BodyController.class.getSimpleName(), def);
  }

}

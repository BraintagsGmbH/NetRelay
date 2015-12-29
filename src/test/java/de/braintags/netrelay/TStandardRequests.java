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

import java.io.File;
import java.util.Set;

import org.junit.Test;

import de.braintags.netrelay.controller.StandarRequestController;
import de.braintags.netrelay.controller.impl.ThymeleafTemplateController;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.util.MultipartUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.test.core.TestUtils;

/**
 * Test the TemplateController of NetRelay
 * 
 * @author Michael Remme
 * 
 */
public class TStandardRequests extends NetRelayBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TStandardRequests.class);

  @Test
  public void testFormURLEncoded(TestContext context) throws Exception {
    resetRoutes();
    MultipartUtil mu = new MultipartUtil();
    addFields(mu);

    testRequest(context, HttpMethod.POST, "/test.html", req -> {
      mu.finish(req);
    } , 200, "OK", null);
    context.assertTrue(StandarRequestController.controllerProcessed, "handler wasn't executed");
    checkFields(context);
  }

  @Test
  public void testFileUploadWithFields(TestContext context) throws Exception {
    resetRoutes();
    String uploadsDir = BodyHandler.DEFAULT_UPLOADS_DIRECTORY;
    String fieldName = "somename";
    String fileName = "somefile.dat";
    String contentType = "application/octet-stream";
    Buffer fileData = TestUtils.randomBuffer(50);
    MultipartUtil mu = new MultipartUtil();
    mu.addFilePart(fieldName, fileName, contentType, fileData);
    addFields(mu);
    testRequest(context, HttpMethod.POST, "/test.html?p1=foo", req -> {
      mu.finish(req);
    } , 200, "OK", null);
    context.assertTrue(StandarRequestController.controllerProcessed, "handler wasn't executed");
    checkFileUpload(context, 1, fileName, fieldName, contentType, uploadsDir, fileData);
    checkFields(context);
    // The body should be set too
    Buffer rawBody = StandarRequestController.bodyBuffer;
    context.assertNotNull(rawBody);
    context.assertTrue(rawBody.length() > fileData.length());
  }

  @Test
  public void testPureFileUpload(TestContext context) throws Exception {
    resetRoutes();
    String uploadsDir = BodyHandler.DEFAULT_UPLOADS_DIRECTORY;
    String fieldName = "somename";
    String fileName = "somefile.dat";
    String contentType = "application/octet-stream";
    Buffer fileData = TestUtils.randomBuffer(50);

    testRequest(context, HttpMethod.POST, "/test.html?p1=foo", req -> {
      MultipartUtil mu = new MultipartUtil();
      mu.addFilePart(fieldName, fileName, contentType, fileData);
      mu.finish(req);
    } , 200, "OK", null);
    context.assertTrue(StandarRequestController.controllerProcessed, "handler wasn't executed");
    checkFileUpload(context, 1, fileName, fieldName, contentType, uploadsDir, fileData);
    // The body should be set too
    Buffer rawBody = StandarRequestController.bodyBuffer;
    context.assertNotNull(rawBody);
    context.assertTrue(rawBody.length() > fileData.length());
  }

  /**
   * @param mu
   */
  private void addFields(MultipartUtil mu) {
    mu.addFormField("origin", "junit-testUserAlias");
    mu.addFormField("login", "admin@foo.bar");
    mu.addFormField("pass word", "admin");
  }

  /**
   * @param context
   */
  private void checkFields(TestContext context) {
    context.assertNotNull(StandarRequestController.attrs);
    context.assertEquals(3, StandarRequestController.attrs.size());
    context.assertEquals("junit-testUserAlias", StandarRequestController.attrs.get("origin"));
    context.assertEquals("admin@foo.bar", StandarRequestController.attrs.get("login"));
    context.assertEquals("admin", StandarRequestController.attrs.get("pass word"));
  }

  private void checkFileUpload(TestContext context, int count, String fileName, String fieldName, String contentType,
      String uploadsDir, Buffer fileData) {
    Set<FileUpload> fileUploads = StandarRequestController.fileUploads;
    context.assertNotNull(fileUploads);
    context.assertEquals(1, fileUploads.size());
    FileUpload upload = fileUploads.iterator().next();
    context.assertEquals(fieldName, upload.name());
    context.assertEquals(fileName, upload.fileName());
    context.assertEquals(contentType, upload.contentType());
    context.assertEquals("binary", upload.contentTransferEncoding());
    context.assertEquals(fileData.length(), (int) upload.size());
    String uploadedFileName = upload.uploadedFileName();
    context.assertTrue(uploadedFileName.startsWith(uploadsDir + File.separator));
    Buffer uploaded = vertx.fileSystem().readFileBlocking(uploadedFileName);
    context.assertEquals(fileData, uploaded);
  }

  /**
   * @throws Exception
   */
  private void resetRoutes() throws Exception {
    StandarRequestController.controllerProcessed = false;
    StandarRequestController.attrs = null;
    StandarRequestController.params = null;
    StandarRequestController.bodyBuffer = null;
    StandarRequestController.fileUploads = null;

    netRelay.getSettings().getRouterDefinitions().addBefore(ThymeleafTemplateController.class.getSimpleName(),
        StandarRequestController.createRouterDefinition());
    netRelay.resetRoutes();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelayBaseTest#initTest()
   */
  @Override
  public void initTest(TestContext context) {
    super.initTest(context);
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

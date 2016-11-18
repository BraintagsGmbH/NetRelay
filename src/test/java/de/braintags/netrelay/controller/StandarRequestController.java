/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.netrelay.controller;

import java.util.Properties;
import java.util.Set;

import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class StandarRequestController extends AbstractController {
  public static MultiMap attrs;
  public static boolean controllerProcessed = false;
  public static MultiMap params;
  public static Set<FileUpload> fileUploads;
  public static Buffer bodyBuffer;

  /**
   * 
   */
  public StandarRequestController() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handleController(RoutingContext event) {
    StandarRequestController.controllerProcessed = true;
    StandarRequestController.attrs = event.request().formAttributes();
    StandarRequestController.params = event.request().params();
    StandarRequestController.fileUploads = event.fileUploads();
    StandarRequestController.bodyBuffer = event.getBody();

    event.response().end();
  }

  public static RouterDefinition createRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName(StandarRequestController.class.getSimpleName());
    def.setControllerClass(StandarRequestController.class);
    def.setHandlerProperties(new Properties());
    return def;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.AbstractController#initProperties(java.util.Properties)
   */
  @Override
  public void initProperties(Properties properties) {
  }

}

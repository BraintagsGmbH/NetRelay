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
package de.braintags.netrelay.controller;

import java.util.List;
import java.util.Properties;

import de.braintags.netrelay.controller.impl.AbstractCaptureController;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class CaptureTestController extends AbstractCaptureController {
  public static final String MAPPER_KEY = "mapper";
  public static final String ID_KEY = "ID";
  public static final String ACTION_KEY = "action";

  public static List<CaptureMap> resolvedCaptureCollections;
  public static String cleanedPath;

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractCaptureController#handle(io.vertx.ext.web.RoutingContext,
   * java.util.List)
   */
  @Override
  protected void handle(RoutingContext context, List<CaptureMap> resolvedCaptureCollections,
      Handler<AsyncResult<Void>> handler) {
    CaptureTestController.resolvedCaptureCollections = resolvedCaptureCollections;
    handler.handle(Future.succeededFuture());
    ;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractCaptureController#internalInitProperties(java.util.Properties)
   */
  @Override
  protected void internalInitProperties(Properties properties) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractCaptureController#cleanPath(io.vertx.ext.web.RoutingContext,
   * java.util.List)
   */
  @Override
  protected String cleanPath(RoutingContext context, List<CaptureMap> resolvedCaptureCollections) {
    cleanedPath = super.cleanPath(context, resolvedCaptureCollections);
    return cleanedPath;
  }

}
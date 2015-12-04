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
package de.braintags.netrelay.controller.impl;

import java.util.List;
import java.util.Properties;

import de.braintags.netrelay.CaptureTestController;
import de.braintags.netrelay.routing.CaptureCollection;
import de.braintags.netrelay.routing.CaptureDefinition;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class PersistenceController extends AbstractCaptureController {

  /**
   * 
   */
  public PersistenceController() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractCaptureController#handle(io.vertx.ext.web.RoutingContext,
   * java.util.List)
   */
  @Override
  protected void handle(RoutingContext context, List<CaptureMap> resolvedCaptureCollections) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractCaptureController#internalInitProperties(java.util.Properties)
   */
  @Override
  protected void internalInitProperties(Properties properties) {
  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName(PersistenceController.class.getSimpleName());
    def.setBlocking(false);
    def.setController(PersistenceController.class);
    def.setHandlerProperties(getDefaultProperties());
    def.setRoutes(new String[] { "/persistenceController/:entity/:ID/:action/read.html" });
    def.setCaptureCollection(createDefaultCaptureCollection());
    return def;
  }

  private static CaptureCollection[] createDefaultCaptureCollection() {
    CaptureDefinition[] defs = new CaptureDefinition[3];
    defs[0] = new CaptureDefinition("entity", CaptureTestController.MAPPER_KEY, false);
    defs[1] = new CaptureDefinition("ID", CaptureTestController.ID_KEY, false);
    defs[2] = new CaptureDefinition("action", CaptureTestController.ACTION_KEY, false);
    CaptureCollection collection = new CaptureCollection();
    collection.setCaptureDefinitions(defs);
    CaptureCollection[] cc = new CaptureCollection[] { collection };
    return cc;
  }

  /**
   * Get the default properties for an implementation
   * 
   * @return
   */
  public static Properties getDefaultProperties() {
    Properties json = new Properties();
    json.put(REROUTE_PROPERTY, "true");
    json.put(AUTO_CLEAN_PATH_PROPERTY, "true");
    return json;
  }

}

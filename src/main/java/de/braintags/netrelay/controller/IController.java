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

import java.util.Properties;

import de.braintags.netrelay.routing.CaptureCollection;
import de.braintags.netrelay.routing.CaptureDefinition;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * An IController is the {@link Handler}, which shall be executed for a certain route definition
 * 
 * @author Michael Remme
 * 
 */
public interface IController extends Handler<RoutingContext> {

  /**
   * Initialize the current controller by the defined properties inside the given {@link JsonObject}.
   * Possible properties are defined inside the implementation of IController
   * 
   * @param vertx
   *          the instance of vertx the system is running under.
   * @param properties
   * @param captureCollection
   *          the {@link CaptureCollection}s for of the current definition. The controller itself decides, wether it
   *          should use it and how to interprete entries of type {@link CaptureDefinition}
   */
  void init(Vertx vertx, Properties properties, CaptureCollection[] captureCollection);

}

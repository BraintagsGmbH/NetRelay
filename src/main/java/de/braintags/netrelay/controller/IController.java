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

import io.vertx.core.Handler;
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
   * @param properties
   */
  void init(Properties properties);

}

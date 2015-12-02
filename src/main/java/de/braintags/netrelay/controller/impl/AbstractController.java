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

import java.util.Properties;

import de.braintags.netrelay.controller.IController;
import io.vertx.core.Vertx;

/**
 * An abstract implementation of {@link IController}
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractController implements IController {
  private Vertx vertx;

  /**
   * 
   */
  public AbstractController() {
  }

  /**
   * @return the vertx
   */
  public final Vertx getVertx() {
    return vertx;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.IController#init(io.vertx.core.Vertx, java.util.Properties)
   */
  @Override
  public final void init(Vertx vertx, Properties properties) {
    this.vertx = vertx;
    initProperties(properties);
  }

  /**
   * Initialize the controller by using the definitions inside the {@link Properties}
   * 
   * @param properties
   *          the properties to be used for init
   */
  public abstract void initProperties(Properties properties);
}

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

import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.controller.IController;
import de.braintags.netrelay.routing.CaptureCollection;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.Vertx;

/**
 * An abstract implementation of {@link IController}
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractController implements IController {
  private Vertx vertx;
  private CaptureCollection[] captureCollection;
  private NetRelay netRelay;

  /**
   * 
   */
  public AbstractController() {
  }

  /**
   * The instance of {@link Vertx} which is used to initialize NetRelay
   * 
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
  public final void init(Vertx vertx, NetRelay netRelay, Properties properties, CaptureCollection[] captureCollection) {
    this.vertx = vertx;
    this.netRelay = netRelay;
    initProperties(properties);
    initCaptureCollection(captureCollection);
  }

  /**
   * Initialize the given {@link CaptureCollection}. The default implementation just stores it inside the current
   * instance
   * 
   * @param captureCollection
   */
  public void initCaptureCollection(CaptureCollection[] captureCollection) {
    this.captureCollection = captureCollection;
  }

  /**
   * Initialize the controller by using the definitions inside the {@link Properties}
   * 
   * @param properties
   *          the properties to be used for init
   */
  public abstract void initProperties(Properties properties);

  /**
   * The {@link CaptureCollection} which was defined inside the {@link RouterDefinition}
   * 
   * @return the captureCollection
   */
  public final CaptureCollection[] getCaptureCollections() {
    return captureCollection;
  }

  /**
   * Get the parent instance of {@link NetRelay}
   * 
   * @return the netRelay
   */
  protected final NetRelay getNetRelay() {
    return netRelay;
  }

}

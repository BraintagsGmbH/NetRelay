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
package de.braintags.netrelay.routing;

import de.braintags.netrelay.controller.IController;
import io.vertx.ext.web.Route;

/**
 * A CaptureDefinition is a translator between the name of a capture, like it was defined inside a {@link Route} and the
 * key, which is defined inside an {@link IController}, who uses parameters of a request.
 * 
 * @author Michael Remme
 * 
 */
public class CaptureDefinition {
  private String captureName;
  private String controllerKey;
  private boolean required = false;

  /**
   * default constructore
   */
  public CaptureDefinition() {
  }

  /**
   * Define a new instance with the captureName and the controllerKey
   * 
   * @param captureName
   *          the captureName, how it was used inside the route definition as capture ( :name)
   * @param controllerKey
   *          the controllerKey, which shall be combined with the parameter. The {@link IController} must "know" what to
   *          do with that
   */
  public CaptureDefinition(String captureName, String controllerKey, boolean required) {
    this.captureName = captureName;
    this.controllerKey = controllerKey;
    this.required = required;
  }

  /**
   * Defines the name of the capture, like defined inside a Route. For a route definition "products/:entity" this would
   * be "entity"
   * 
   * @return the captureName
   */
  public final String getCaptureName() {
    return captureName;
  }

  /**
   * Defines the name of the capture, like defined inside a Route. For a route definition "products/:entity" this would
   * be "entity"
   * 
   * @param captureName
   *          the captureName to set
   */
  public final void setCaptureName(String captureName) {
    this.captureName = captureName;
  }

  /**
   * The key of the {@link IController}, which is used inside the controller to define a member of an action
   * 
   * @return the controllerKey
   */
  public final String getControllerKey() {
    return controllerKey;
  }

  /**
   * The key of the {@link IController}, which is used inside the controller to define a member of an action
   * 
   * @param controllerKey
   *          the controllerKey to set
   */
  public final void setControllerKey(String controllerKey) {
    this.controllerKey = controllerKey;
  }

  /**
   * Defines wether the current parameter is required. If required, and a request doesn't contain it, the request will
   * fail
   * 
   * @return the required
   */
  public final boolean isRequired() {
    return required;
  }

  /**
   * Defines wether the current parameter is required. If required, and a request doesn't contain it, the request will
   * fail
   * 
   * @param required
   *          the required to set
   */
  public final void setRequired(boolean required) {
    this.required = required;
  }

}

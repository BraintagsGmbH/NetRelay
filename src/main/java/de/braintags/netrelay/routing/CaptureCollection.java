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

/**
 * A CaptureCollection defines all captures for one action, which are belonging together.
 * For a router definition like "/catalogue/products/:entity/:productid/:action/" one would
 * define a CaptureCollection with 3 members:
 * 
 * entity / mapper
 * productid / identifyer
 * action / action
 * 
 * A controller would extract the values and would execute the correct action, because he "knows" what to do with the
 * definitions.
 * 
 * If a concrete URL would be then for example: /catalogue/products/article/12/search he would search the
 * mapper article with the id 12 and store it inside the context of the request
 * 
 * 
 * @author Michael Remme
 * 
 */
public class CaptureCollection {
  private CaptureDefinition[] captureDefinitions;

  /**
   * 
   */
  public CaptureCollection() {
  }

  /**
   * The list of capture definitions, which are belonging together
   * 
   * @return the captureDefinitions
   */
  public final CaptureDefinition[] getCaptureDefinitions() {
    return captureDefinitions;
  }

  /**
   * The list of capture definitions, which are belonging together
   * 
   * @param captureDefinitions
   *          the captureDefinitions to set
   */
  public final void setCaptureDefinitions(CaptureDefinition[] captureDefinitions) {
    this.captureDefinitions = captureDefinitions;
  }

  /**
   * Get the {@link CaptureDefinition} where the controller key equals the given key
   * 
   * @param key
   *          the key to search for
   * @return a suitable instance of {@link CaptureDefinition} or null
   */
  public CaptureDefinition getCaptureDefinition(String controllerKey) {
    for (CaptureDefinition def : captureDefinitions) {
      if (def.getControllerKey().equals(controllerKey)) {
        return def;
      }
    }
    return null;
  }

  /**
   * Get the keyname of the capture for the given controller key
   * 
   * @param controllerKey
   *          the controller key to search for
   * @return the suitable capture name or null
   */
  public String getCaptureName(String controllerKey) {
    CaptureDefinition def = getCaptureDefinition(controllerKey);
    return def == null ? null : def.getCaptureName();
  }

}

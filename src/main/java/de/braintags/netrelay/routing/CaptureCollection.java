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

}

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
package de.braintags.netrelay.exception;

/**
 * Exception is thrown when a Property was required but not found
 * 
 * @author Michael Remme
 * 
 */
public class PropertyRequiredException extends RuntimeException {

  /**
   * @param message
   */
  public PropertyRequiredException(String propertyName) {
    super("Property was not found: " + propertyName);
  }

}

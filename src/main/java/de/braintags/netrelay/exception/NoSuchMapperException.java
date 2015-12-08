/*
 * #%L
 * vertx-pojongo
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

import de.braintags.netrelay.init.MappingDefinitions;

/**
 * Exception is thrown, when a mapper is requested, where for no definition is found inside {@link MappingDefinitions}
 * 
 * @author Michael Remme
 * 
 */
public class NoSuchMapperException extends RuntimeException {
  private static final String MESSAGE = "No mapper found for name '%s'";

  /**
   * @param message
   * @param cause
   */
  public NoSuchMapperException(String mapperName, Throwable cause) {
    super(String.format(MESSAGE, mapperName), cause);
  }

  /**
   * @param message
   */
  public NoSuchMapperException(String mapperName) {
    super(String.format(MESSAGE, mapperName));
  }

}

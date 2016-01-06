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

/**
 * This exception is thrown, when a problem with a filename was detected
 * 
 * @author Michael Remme
 * 
 */
public class FileNameException extends RuntimeException {

  /**
   * 
   */
  public FileNameException() {
  }

  /**
   * @param message
   */
  public FileNameException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public FileNameException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public FileNameException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public FileNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}

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
package de.braintags.netrelay.controller.impl.authentication;

/**
 * Errorcodes, which can occur on the process password lost
 * 
 * @author Michael Remme
 * 
 */
public enum PasswordLostCode {

  OK,
  /**
   * Error that marks, that an email is required
   */
  EMAIL_REQUIRED,
  NO_ACCOUNT,
  CONFIRMATION_FAILURE,
  INVALID_EMAIL;
}

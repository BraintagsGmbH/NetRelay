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
 * Errorcodes, which can occur on registraction
 * 
 * @author Michael Remme
 * 
 */
public enum RegistrationCode {

  OK,
  /**
   * Error that marks, that an email adress exists already in the system
   */
  EMAIL_EXISTS,
  EMAIL_REQUIRED,
  PASSWORD_REQUIRED,
  INVALID_EMAIL;
}

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
package de.braintags.netrelay.model;

import io.vertx.core.http.HttpServerRequest;

/**
 * RegisterClaim stores all information about a registration, performed by an {@link IAuthenticatable} ( Member etc ).
 * It is used as source to send the double opt in message and to improve the registration, when the user clicked the
 * link sent by the double opt in process
 * 
 * @author Michael Remme
 * 
 */
public class RegisterClaim extends PasswordLostClaim {
  public String password;

  public RegisterClaim() {
  }

  /**
   * Creates an instance, where all needed information of the request to register are stored
   * 
   * @param request
   */
  public RegisterClaim(String email, String password, HttpServerRequest request) {
    super(email, request);
    this.password = password;
  }

}

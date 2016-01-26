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

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;

/**
 * RegisterClaim stores all information about a registration, performed by a customer or member.
 * It is used as source to send the double opt in message and to improve the registration, when the user clicked the
 * link sent by the double opt in process
 * 
 * @author Michael Remme
 * 
 */
public class RegisterClaim extends AbstractRecord {
  public MultiMap formAttributes;
  public MultiMap urlParameter;

  public RegisterClaim() {
  }

  /**
   * Creates an instance, where all needed information of the request to register are stored
   * 
   * @param request
   */
  public RegisterClaim(HttpServerRequest request) {
    formAttributes = request.formAttributes();
    urlParameter = request.params();
  }

}

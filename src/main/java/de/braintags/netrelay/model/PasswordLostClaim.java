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
package de.braintags.netrelay.model;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;

/**
 * PasswordLostClaim stores all information about a password lost process, performed by an {@link IAuthenticatable} (
 * Member etc ).
 * It is used as source to send the confirmation message and to give the ability to reset the password, when the user
 * clicked the link, which was contained inside the message
 * 
 * @author Michael Remme
 * 
 */
public class PasswordLostClaim extends AbstractRecord {
  public String email;
  public boolean active = true;
  private IAuthenticatable user;
  public Map<String, String> requestParameter = new HashMap<>();

  /**
   * 
   */
  public PasswordLostClaim() {
  }

  /**
   * @param email
   * @param password
   * @param request
   */
  public PasswordLostClaim(String email, HttpServerRequest request) {
    this.email = email;
    transfer(request.formAttributes(), requestParameter);
    transfer(request.params(), requestParameter);
  }

  private void transfer(MultiMap mm, Map<String, String> destination) {
    mm.entries().forEach(entry -> destination.put(entry.getKey(), entry.getValue()));
  }

}

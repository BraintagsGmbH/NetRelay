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
package de.braintags.netrelay.controller;

/**
 * An action, which can be performed by a request
 * 
 * 
 * @author Michael Remme
 *
 */
public enum Action {
  /**
   * request for an action to insert a record
   */
  INSERT(),
  /**
   * request for an action to update a record
   */
  UPDATE(),
  /**
   * request for an action to display a record or a list of records
   */
  DISPLAY(),
  /**
   * request for an action to delete a record
   */
  DELETE(),
  /**
   * request for an action to perform no action. This action could be used, if one template shall be used for inserting
   * a new record and displaying the inserted result
   */
  NONE();

}
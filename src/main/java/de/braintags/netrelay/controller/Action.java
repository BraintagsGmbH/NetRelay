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
  INSERT(
      "INSERT", 'C'),
  /**
   * request for an action to update a record
   */
  UPDATE(
      "UPDATE", 'U'),
  /**
   * request for an action to display a record or a list of records
   */
  DISPLAY(
      "DISPLAY", 'R'),
  /**
   * request for an action to delete a record
   */
  DELETE(
      "DELETE", 'D'),
  /**
   * request for an action to perform no action. This action could be used, if one template shall be used for inserting
   * a new record and displaying the inserted result
   */
  NONE(
      "NONE", ' ');

  private String value;
  private char CRUD;

  Action(String value, char CRUD) {
    this.value = value;
  }

  /**
   * Get the char, which is describing the action as CRUD
   * 
   * @return
   */
  public char getCRUD() {
    return CRUD;
  }

  @Override
  public String toString() {
    return value;
  }
}
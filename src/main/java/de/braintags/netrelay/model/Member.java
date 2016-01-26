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

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class Member implements IAuthenticatable {
  public static final String CURRENT_USER_PROPERTY = "currentUser";
  @Id
  private String id;
  private String userName;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String gender;

  /**
   * @return the userName
   */
  public final String getUserName() {
    return userName;
  }

  /**
   * @param userName
   *          the userName to set
   */
  public final void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * @return the firstName
   */
  public final String getFirstName() {
    return firstName;
  }

  /**
   * @param firstName
   *          the firstName to set
   */
  public final void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * @return the lastName
   */
  public final String getLastName() {
    return lastName;
  }

  /**
   * @param lastName
   *          the lastName to set
   */
  public final void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * @return the email
   */
  @Override
  public final String getEmail() {
    return email;
  }

  /**
   * @param email
   *          the email to set
   */
  @Override
  public final void setEmail(String email) {
    this.email = email;
  }

  /**
   * @return the password
   */
  @Override
  public final String getPassword() {
    return password;
  }

  /**
   * @param password
   *          the password to set
   */
  @Override
  public final void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return the gender
   */
  public final String getGender() {
    return gender;
  }

  /**
   * @param gender
   *          the gender to set
   */
  public final void setGender(String gender) {
    this.gender = gender;
  }

  /**
   * @return the id
   */
  public final String getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public final void setId(String id) {
    this.id = id;
  }

}

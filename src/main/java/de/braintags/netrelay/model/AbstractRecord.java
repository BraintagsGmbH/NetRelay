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

import java.sql.Timestamp;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeSave;
import de.braintags.io.vertx.util.ObjectUtil;

/**
 * Abstract implementation for a record, which takes care about modification date and other basic information
 * 
 * @author Michael Remme
 * 
 */
@Entity
public abstract class AbstractRecord {
  @Id
  public String id;
  public Timestamp createdOn = new Timestamp(System.currentTimeMillis());
  public Timestamp modifiedOn;

  @BeforeSave
  public void beforeSave() {
    modifiedOn = new Timestamp(System.currentTimeMillis());
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof AbstractRecord && ObjectUtil.isEqual(((AbstractRecord) o).id, id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    String str = getClass().getName() + String.valueOf(id);
    return str.hashCode();
  }

}

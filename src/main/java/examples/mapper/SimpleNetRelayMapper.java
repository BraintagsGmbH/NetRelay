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
package examples.mapper;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class SimpleNetRelayMapper {
  @Id
  public String id;
  public String name;

  /**
   * 
   */
  public SimpleNetRelayMapper() {
  }

}

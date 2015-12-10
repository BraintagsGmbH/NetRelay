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
package de.braintags.netrelay.exception;

import de.braintags.io.vertx.pojomapper.mapping.IMapper;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class FieldNotFoundException extends RuntimeException {
  private static final String MESSAGE = "Could not find field %s in mapper %s";

  /**
   * @param message
   * @param cause
   */
  public FieldNotFoundException(IMapper mapper, String fieldname, Throwable cause) {
    super(formatMessage(mapper, fieldname), cause);
  }

  /**
   * @param message
   */
  public FieldNotFoundException(IMapper mapper, String fieldname) {
    super(formatMessage(mapper, fieldname));
  }

  private static final String formatMessage(IMapper mapper, String fieldname) {
    return String.format(MESSAGE, fieldname, mapper.getMapperClass().toString());
  }
}

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
package de.braintags.netrelay.typehandler;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.BooleanTypeHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class HttpBooleanTypeHandler extends BooleanTypeHandler {

  /**
   * @param typeHandlerFactory
   */
  public HttpBooleanTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory);
  }

  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    String bv = source == null ? "false" : source.toString();
    bv = bv.equalsIgnoreCase("on") ? "true" : bv;
    success(Boolean.valueOf(bv), resultHandler);
  }

}

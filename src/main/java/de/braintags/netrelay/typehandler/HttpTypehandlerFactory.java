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

import de.braintags.vertx.jomnigate.typehandler.stringbased.StringTypeHandlerFactory;
import de.braintags.vertx.jomnigate.typehandler.stringbased.handlers.BooleanTypeHandler;
import de.braintags.vertx.jomnigate.typehandler.stringbased.handlers.GeoPointTypeHandler;

/**
 * A TypeHandlerFactory, which reacts with information coming across http requests
 * 
 * @author Michael Remme
 * 
 */
public class HttpTypehandlerFactory extends StringTypeHandlerFactory {

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.json.typehandler.JsonTypeHandlerFactory#init()
   */
  @Override
  protected void init() {
    super.init();
    remove(BooleanTypeHandler.class);
    getDefinedTypeHandlers().add(0, new HttpBooleanTypeHandler(this));
    replace(GeoPointTypeHandler.class, new HttpGeoPointTypeHandler(this));
  }
}

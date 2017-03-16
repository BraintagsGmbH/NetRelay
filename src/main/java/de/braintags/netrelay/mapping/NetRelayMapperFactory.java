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
package de.braintags.netrelay.mapping;

import java.util.Map;

import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.typehandler.HttpTypehandlerFactory;
import de.braintags.vertx.jomnigate.json.mapping.JsonPropertyMapperFactory;
import de.braintags.vertx.jomnigate.mapping.IStoreObjectFactory;
import de.braintags.vertx.jomnigate.mapping.impl.MapperFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;

/**
 * NetRelayMapperFactory is used as factory for mapper definitions for the communication with the underlaying
 * template engine and the data coming inside an http request. It is using an {@link ITypeHandlerFactory} which is
 * string based
 * 
 * @author Michael Remme
 * 
 */
public class NetRelayMapperFactory extends MapperFactory<Map<String, String>> {
  private NetRelay netRelay;

  public NetRelayMapperFactory(NetRelay netrelay) {
    super(null, new HttpTypehandlerFactory(), new JsonPropertyMapperFactory());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.impl.AbstractMapperFactory#getStoreObjectFactory()
   */
  @Override
  public IStoreObjectFactory<Map<String, String>> getStoreObjectFactory() {
    return netRelay.getStoreObjectFactory();
  }

}

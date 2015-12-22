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

import de.braintags.io.vertx.pojomapper.json.mapping.JsonPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.impl.MapperFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.typehandler.HttpTypehandlerFactory;

/**
 * NetRelayMapperFactory is used as factory for mapper definitions for the communication with the underlaying
 * template engine and the data coming inside an http request. It is using an {@link ITypeHandlerFactory} which is
 * string based
 * 
 * @author Michael Remme
 * 
 */
public class NetRelayMapperFactory extends MapperFactory {

  public NetRelayMapperFactory(NetRelay netrelay) {
    super(null, new HttpTypehandlerFactory(), new JsonPropertyMapperFactory(),
        new NetRelayStoreObjectFactory(netrelay));
  }

}

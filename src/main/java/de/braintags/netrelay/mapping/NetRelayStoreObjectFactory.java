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

import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.impl.AbstractStoreObjectFactory;
import de.braintags.netrelay.NetRelay;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * The {@link NetRelayStoreObjectFactory} is used, when instances shall be created from the information of a request.
 * 
 * @author Michael Remme
 * 
 */
public class NetRelayStoreObjectFactory extends AbstractStoreObjectFactory {
  private NetRelay netRelay;

  public NetRelayStoreObjectFactory(NetRelay netRelay) {
    this.netRelay = netRelay;
  }

  @Override
  public void createStoreObject(IMapper mapper, Object entity, Handler<AsyncResult<IStoreObject<?>>> handler) {
    NetRelayStoreObject storeObject = new NetRelayStoreObject(mapper, entity);
    storeObject.initFromEntity(initResult -> {
      if (initResult.failed()) {
        handler.handle(Future.failedFuture(initResult.cause()));
      } else {
        handler.handle(Future.succeededFuture(storeObject));
      }
    });
  }

  @Override
  public void createStoreObject(Object storedObject, IMapper mapper, Handler<AsyncResult<IStoreObject<?>>> handler) {
    NetRelayStoreObject storeObject = new NetRelayStoreObject((Map<String, String>) storedObject, mapper, netRelay);
    storeObject.initToEntity(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture(storeObject));
      }
    });
  }

}

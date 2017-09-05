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
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IStoreObject;
import de.braintags.vertx.jomnigate.mapping.impl.AbstractStoreObjectFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * The {@link NetRelayStoreObjectFactory} is used, when instances shall be created from the information of a request.
 * 
 * @author Michael Remme
 * 
 */
public class NetRelayStoreObjectFactory extends AbstractStoreObjectFactory<Map<String, String>> {
  private final NetRelay netRelay;

  public NetRelayStoreObjectFactory(final NetRelay netRelay) {
    this.netRelay = netRelay;
  }

  @Override
  public <T> void createStoreObject(final IMapper<T> mapper, final T entity,
      final Handler<AsyncResult<IStoreObject<T, Map<String, String>>>> handler) {
    NetRelayStoreObject<T> storeObject = new NetRelayStoreObject<>(mapper, entity);
    storeObject.initFromEntity(initResult -> {
      if (initResult.failed()) {
        handler.handle(Future.failedFuture(initResult.cause()));
      } else {
        handler.handle(Future.succeededFuture(storeObject));
      }
    });
  }

  @Override
  public <T> void createStoreObject(final Map<String, String> storedObject, final IMapper<T> mapper,
      final Handler<AsyncResult<IStoreObject<T, Map<String, String>>>> handler) {
    createStoreObject(storedObject, null, mapper, handler);
  }

  /**
   * This method is called in cases, where a subobject needs to be updated, which can't be loaded from the datastore
   * 
   * @param storedObject
   * @param entity
   * @param mapper
   * @param handler
   */
  public <T> void createStoreObject(final Map<String, String> storedObject, final T entity, final IMapper<T> mapper,
      final Handler<AsyncResult<IStoreObject<T, Map<String, String>>>> handler) {
    NetRelayStoreObject<T> storeObject = new NetRelayStoreObject<>(storedObject, entity, mapper, netRelay);
    storeObject.initToEntity(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture(storeObject));
      }
    });
  }

}

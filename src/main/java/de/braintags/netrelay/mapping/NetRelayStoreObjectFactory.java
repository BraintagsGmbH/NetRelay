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

import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.impl.AbstractStoreObjectFactory;
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

  /**
   * 
   */
  public NetRelayStoreObjectFactory() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory#createStoreObject(de.braintags.io.vertx.pojomapper.
   * mapping.IMapper, java.lang.Object, io.vertx.core.Handler)
   */
  @Override
  public void createStoreObject(IMapper mapper, Object entity, Handler<AsyncResult<IStoreObject<?>>> handler) {
    handler.handle(Future.failedFuture(new UnsupportedOperationException()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory#createStoreObject(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IMapper, io.vertx.core.Handler)
   */
  @Override
  public void createStoreObject(Object storedObject, IMapper mapper, Handler<AsyncResult<IStoreObject<?>>> handler) {
    handler.handle(Future.failedFuture(new UnsupportedOperationException()));
  }

}

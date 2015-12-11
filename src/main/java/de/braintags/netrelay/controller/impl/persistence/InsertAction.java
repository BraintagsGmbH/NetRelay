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
package de.braintags.netrelay.controller.impl.persistence;

import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.netrelay.controller.impl.AbstractCaptureController.CaptureMap;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class InsertAction extends AbstractAction {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(InsertAction.class);

  /**
   * 
   */
  public InsertAction(PersistenceController persitenceController) {
    super(persitenceController);
  }

  @Override
  void handle(String entityName, RoutingContext context, CaptureMap map, Handler<AsyncResult<Void>> handler) {
    IMapper mapper = getMapper(entityName);
    getPersistenceController().getMapperFactory().getStoreObjectFactory().createStoreObject(context, mapper, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object ob = result.result().getEntity();
        store(ob, entityName, context, mapper, handler);
      }
    });
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void store(Object ob, String entityName, RoutingContext context, IMapper mapper,
      Handler<AsyncResult<Void>> handler) {
    IWrite write = getPersistenceController().getNetRelay().getDatastore().createWrite(mapper.getMapperClass());
    write.add(ob);
    write.save(res -> {
      AsyncResult<IWriteResult> result = (AsyncResult<IWriteResult>) res;
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        LOGGER.info("adding new entity to context with key " + entityName);
        context.put(entityName, ob);
        handler.handle(Future.succeededFuture());
      }
    });
  }

}

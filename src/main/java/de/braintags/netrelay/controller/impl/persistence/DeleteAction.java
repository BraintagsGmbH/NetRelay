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

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
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
public class DeleteAction extends AbstractAction {

  /**
   * 
   */
  public DeleteAction(PersistenceController persitenceController) {
    super(persitenceController);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  void handle(String entityName, RoutingContext context, CaptureMap captureMap, Handler<AsyncResult<Void>> handler) {
    IMapper mapper = getMapper(entityName);
    String id = captureMap.get(PersistenceController.ID_CAPTURE_KEY);
    IDataStore datastore = getPersistenceController().getNetRelay().getDatastore();
    IDelete<?> delete = datastore.createDelete(mapper.getMapperClass());
    IQuery query = getPersistenceController().getNetRelay().getDatastore().createQuery(mapper.getMapperClass());
    query.field(mapper.getIdField().getName()).is(id);
    delete.setQuery(query);
    delete.delete(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture());
      }
    });
  }

}

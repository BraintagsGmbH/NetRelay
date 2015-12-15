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

import java.util.Arrays;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.exception.NoSuchRecordException;
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
public class DisplayAction extends AbstractAction {
  private static final String ERRORMESSAGE = "could not find record with ID %s";

  /**
   * 
   */
  public DisplayAction(PersistenceController persitenceController) {
    super(persitenceController);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.persistence.AbstractAction#handle(io.vertx.ext.web.RoutingContext,
   * de.braintags.netrelay.controller.impl.AbstractCaptureController.CaptureMap)
   */
  @Override
  void handle(String entityName, RoutingContext context, CaptureMap map, Handler<AsyncResult<Void>> handler) {
    IMapper mapper = getMapper(entityName);
    String id = map.get(PersistenceController.ID_KEY);
    if (id == null) {
      handleList(entityName, context, mapper, id, handler);
    } else {
      handleSingleRecord(entityName, context, mapper, id, handler);
    }
  }

  protected void handleList(String entityName, RoutingContext context, IMapper mapper, String id,
      Handler<AsyncResult<Void>> handler) {
    IQuery<?> query = getPersistenceController().getNetRelay().getDatastore().createQuery(mapper.getMapperClass());
    query.execute(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        IQueryResult<?> qr = result.result();
        qr.toArray(arr -> {
          if (arr.failed()) {
            handler.handle(Future.failedFuture(arr.cause()));
          } else {
            addToContext(context, entityName, Arrays.asList(arr.result()));
            handler.handle(Future.succeededFuture());
          }
        });
      }
    });
  }

  protected void handleSingleRecord(String entityName, RoutingContext context, IMapper mapper, String id,
      Handler<AsyncResult<Void>> handler) {
    IQuery<?> query = getPersistenceController().getNetRelay().getDatastore().createQuery(mapper.getMapperClass());
    query.field(mapper.getIdField().getName()).is(id);
    query.execute(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        IQueryResult<?> qr = result.result();
        if (qr.isEmpty()) {
          handler.handle(Future.failedFuture(new NoSuchRecordException(String.format(ERRORMESSAGE, id))));
        } else {
          qr.iterator().next(ir -> {
            if (ir.failed()) {
              handler.handle(Future.failedFuture(ir.cause()));
            } else {
              saveObjectInDatastore(ir.result(), entityName, context, mapper, handler);
            }
          });
        }
      }
    });
  }

}

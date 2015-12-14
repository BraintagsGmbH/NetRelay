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
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.netrelay.controller.impl.AbstractCaptureController.CaptureMap;
import de.braintags.netrelay.exception.NoSuchMapperException;
import de.braintags.netrelay.init.MappingDefinitions;
import de.braintags.netrelay.init.Settings;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * An abstract implementation for the different possible actions of {@link PersistenceController}
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractAction {
  private PersistenceController persitenceController;
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractAction.class);

  /**
   * 
   */
  public AbstractAction(PersistenceController persitenceController) {
    this.persitenceController = persitenceController;
  }

  protected PersistenceController getPersistenceController() {
    return persitenceController;
  }

  /**
   * Retrive the {@link IMapper} which is specified by the given mapperName.
   * 
   * @param mapperName
   *          the name of the mapper. This name mus exist as definition inside the {@link MappingDefinitions} of the
   *          {@link Settings}
   * @return a mapper from the internal {@link IMapperFactory}
   */
  protected IMapper getMapper(String mapperName) {
    Class mapperClass = persitenceController.getNetRelay().getSettings().getMappingDefinitions()
        .getMapperClass(mapperName);
    if (mapperClass == null) {
      throw new NoSuchMapperException(mapperName);
    }
    return persitenceController.getMapperFactory().getMapper(mapperClass);
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

  /**
   * The sub module handles the request with the appropriate action
   * 
   * @param entityName
   *          the name of the entity defined by the request
   * @param context
   *          the context, where to get the request data from for instance
   * @param map
   *          the resolved {@link CaptureMap}
   * @param handler
   *          the handler to be informed about the result
   */
  abstract void handle(String entityName, RoutingContext context, CaptureMap map, Handler<AsyncResult<Void>> handler);
}

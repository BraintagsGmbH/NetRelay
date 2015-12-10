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
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.netrelay.controller.impl.AbstractCaptureController.CaptureMap;
import io.vertx.core.AsyncResult;
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
   * The sub module handles the request with the appropriate action
   * 
   * @param datastore
   *          the {@link IDataStore} to be used for reading / writing objects
   * @param entityName
   *          the name of the entity defined by the request
   * @param mapper
   *          the {@link IMapper} to be used
   * @param context
   *          the context, where to get the request data from for instance
   * @param map
   *          the resolved {@link CaptureMap}
   * @param handler TODO
   */
  abstract void handle(IDataStore datastore, String entityName, IMapper mapper, RoutingContext context, CaptureMap map, Handler<AsyncResult<Void>> handler);
}

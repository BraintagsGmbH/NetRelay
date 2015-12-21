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

import de.braintags.netrelay.controller.impl.AbstractCaptureController.CaptureMap;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class NoneAction extends AbstractAction {

  /**
   * @param persitenceController
   */
  public NoneAction(PersistenceController persitenceController) {
    super(persitenceController);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.persistence.AbstractAction#handle(java.lang.String,
   * io.vertx.ext.web.RoutingContext, de.braintags.netrelay.controller.impl.AbstractCaptureController.CaptureMap,
   * io.vertx.core.Handler)
   */
  @Override
  void handle(String entityName, RoutingContext context, CaptureMap map, Handler<AsyncResult<Void>> handler) {
    // do really nothing?
  }

}

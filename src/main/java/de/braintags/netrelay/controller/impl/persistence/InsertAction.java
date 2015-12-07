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
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class InsertAction extends AbstractAction {

  /**
   * 
   */
  public InsertAction(PersistenceController persitenceController) {
    super(persitenceController);
  }

  @Override
  void handle(RoutingContext context, CaptureMap map) {
    String mapper = map.get(PersistenceController.MAPPER_KEY);

    throw new UnsupportedOperationException();
  }

}

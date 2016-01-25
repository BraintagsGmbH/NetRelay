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

import java.util.Map;

import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.util.exception.ParameterRequiredException;
import de.braintags.netrelay.controller.impl.AbstractCaptureController.CaptureMap;
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class UpdateAction extends InsertAction {

  /**
   * 
   */
  public UpdateAction(PersistenceController persitenceController) {
    super(persitenceController);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.persistence.InsertAction#extractProperties(java.lang.String,
   * io.vertx.ext.web.RoutingContext)
   */
  @Override
  protected Map<String, String> extractProperties(String entityName, CaptureMap captureMap, RoutingContext context,
      IMapper mapper) {
    Map<String, String> map = super.extractProperties(entityName, captureMap, context, mapper);
    String id = captureMap.get(PersistenceController.ID_KEY);
    if (id == null || id.hashCode() == 0) {
      throw new ParameterRequiredException("ID");
    }
    map.put(mapper.getIdField().getName().toLowerCase(), id);
    return map;
  }

}

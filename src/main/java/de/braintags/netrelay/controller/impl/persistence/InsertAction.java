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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.netrelay.controller.impl.AbstractCaptureController.CaptureMap;
import de.braintags.netrelay.exception.FieldNotFoundException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
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

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  void handle(IDataStore datastore, String entityName, IMapper mapper, RoutingContext context, CaptureMap map,
      Handler<AsyncResult<Void>> handler) {
    Map<String, String> valueMap = extractProperties(entityName, context);
    Object mo = mapper.getObjectFactory().createInstance(mapper.getMapperClass());
    Iterator<String> it = valueMap.keySet().iterator();
    while (it.hasNext()) {
      String key = it.next();
      String value = valueMap.get(key);
      IField field = mapper.getField(key);
      if (field == null) {
        throw new FieldNotFoundException(mapper, key);
      }

      field.getPropertyAccessor().writeData(mo, value);
    }
    IWrite write = datastore.createWrite(mapper.getMapperClass());
    write.add(mo);
    write.save(res -> {
      AsyncResult<IWriteResult> result = (AsyncResult<IWriteResult>) res;
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        context.put(entityName, mo);
      }
    });
  }

  /**
   * Extract the properties from the request, where the name starts with the entity name, which shall be handled by the
   * current request
   * 
   * @param entityName
   *          the name, like it was specified by the parameter {@link PersistenceController#MAPPER_KEY}
   * @param context
   *          the {@link RoutingContext} of the request
   * @return the key / values of the request, where the key starts with "entityName.". The key is reduced to the pure
   *         name
   */
  protected Map<String, String> extractProperties(String entityName, RoutingContext context) {
    String startKey = entityName.toLowerCase() + ".";
    Map<String, String> map = new HashMap<>();

    MultiMap attrs = context.request().formAttributes();
    Iterator<Entry<String, String>> it = attrs.iterator();
    while (it.hasNext()) {
      Entry<String, String> entry = it.next();
      String key = entry.getKey();
      if (key.toLowerCase().startsWith(startKey)) {
        String pureKey = key.substring(startKey.length());
        String value = entry.getValue();
        map.put(pureKey, value);
      }
    }
    return map;
  }

}

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
package de.braintags.netrelay.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.exception.NoSuchRecordException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IObjectReference;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.netrelay.NetRelay;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * NetRelayStoreObject is the bridge between http requests and mapper objects
 * 
 * @author Michael Remme
 * 
 */
public class NetRelayStoreObject implements IStoreObject<Map<String, String>> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(NetRelayStoreObject.class);

  private IMapper mapper;
  private Object entity = null;
  private Collection<IObjectReference> objectReferences = new ArrayList<IObjectReference>();
  private Map<String, String> requestMap = new HashMap<>();
  private NetRelay netRelay;

  /**
   * Constructor to create an instance from a mapper
   * 
   * @param mapper
   *          the {@link IMapper} to be used
   * @param entity
   *          the entity to be used
   */
  public NetRelayStoreObject(IMapper mapper, Object entity) {
    if (mapper == null)
      throw new NullPointerException("Mapper must not be null");
    this.mapper = mapper;
    this.entity = entity;
  }

  /**
   * Constructor to create an instance from a request
   * 
   * @param requestMap
   *          a {@link Map} with key value pairs, which are describing the object properties
   * @param mapper
   *          the mapper to be used
   */
  public NetRelayStoreObject(Map<String, String> requestMap, IMapper mapper, NetRelay netRelay) {
    Objects.requireNonNull(mapper, "Mapper must not be null");
    Objects.requireNonNull(requestMap, "requestMap must not be null");
    this.mapper = mapper;
    this.requestMap = requestMap;
    this.netRelay = netRelay;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IStoreObject#get(de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public Object get(IField field) {
    return requestMap.get(field.getName().toLowerCase());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.IStoreObject#hasProperty(de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public boolean hasProperty(IField field) {
    return requestMap.containsKey(field.getName().toLowerCase());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IStoreObject#put(de.braintags.io.vertx.pojomapper.mapping.IField,
   * java.lang.Object)
   */
  @Override
  public IStoreObject<Map<String, String>> put(IField field, Object value) {
    requestMap.put(field.getName().toLowerCase(), String.valueOf(value));
    return this;
  }

  @Override
  public Map getContainer() {
    return requestMap;
  }

  /**
   * @return the mapper
   */
  public IMapper getMapper() {
    return mapper;
  }

  @Override
  public Object getEntity() {
    if (entity == null) {
      String message = String.format("Internal Entity is not initialized; call method %s.initToEntity first ",
          getClass().getName());
      throw new NullPointerException(message);
    }
    return entity;
  }

  /**
   * Initialize the internal entity from the information previously read from the datastore.
   * 
   * @param handler
   */
  public final void initToEntity(Handler<AsyncResult<Void>> handler) {
    createEntity(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object tmpObject = result.result();
        LOGGER.debug("start initToEntity");
        iterateFields(tmpObject, fieldResult -> {
          if (fieldResult.failed()) {
            handler.handle(fieldResult);
            return;
          }
          iterateObjectReferences(tmpObject, orResult -> {
            if (orResult.failed()) {
              handler.handle(orResult);
              return;
            }
            finishToEntity(tmpObject, handler);
            LOGGER.debug("finished initToEntity");
          });
        });
      }
    });
  }

  @SuppressWarnings({ "rawtypes" })
  private void createEntity(Handler<AsyncResult<Object>> handler) {
    if (hasProperty(getMapper().getIdField())) {
      Object id = get(getMapper().getIdField());
      IQuery<?> query = netRelay.getDatastore().createQuery(getMapper().getMapperClass());
      query.field(query.getMapper().getIdField().getName()).is(id);
      query.execute(qrr -> {
        if (qrr.failed()) {
          handler.handle(Future.failedFuture(qrr.cause()));
        } else {
          IQueryResult<?> qr = qrr.result();
          if (!qr.iterator().hasNext()) {
            handler.handle(Future.failedFuture(new NoSuchRecordException("Could not find record with ID " + id)));
          } else {
            qr.iterator().next(ir -> {
              if (ir.failed()) {
                handler.handle(Future.failedFuture(ir.cause()));
              } else {
                handler.handle(Future.succeededFuture(ir.result()));
              }
            });
          }
        }
      });
    } else {
      Object returnObject = getMapper().getObjectFactory().createInstance(getMapper().getMapperClass());
      handler.handle(Future.succeededFuture(returnObject));
    }
  }

  protected void finishToEntity(Object tmpObject, Handler<AsyncResult<Void>> handler) {
    this.entity = tmpObject;
    try {
      handler.handle(Future.succeededFuture());
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  protected final void iterateFields(Object tmpObject, Handler<AsyncResult<Void>> handler) {
    LOGGER.debug("start iterateFields");
    Set<String> fieldNames = getMapper().getFieldNames();
    CounterObject<Void> co = new CounterObject<>(fieldNames.size(), handler);
    for (String fieldName : fieldNames) {
      IField field = getMapper().getField(fieldName);
      LOGGER.debug("handling field " + field.getFullName());
      field.getPropertyMapper().fromStoreObject(tmpObject, this, field, result -> {
        if (result.failed()) {
          co.setThrowable(result.cause());
          return;
        }
        if (co.reduce()) {
          LOGGER.debug("field counter finished");
          handler.handle(Future.succeededFuture());
        }
      });
    }
  }

  protected void iterateObjectReferences(Object tmpObject, Handler<AsyncResult<Void>> handler) {
    LOGGER.debug("start iterateObjectReferences");
    if (getObjectReferences().isEmpty()) {
      LOGGER.debug("nothing to do");
      handler.handle(Future.succeededFuture());
      return;
    }
    Collection<IObjectReference> refs = getObjectReferences();
    CounterObject<Void> co = new CounterObject<>(refs.size(), handler);
    for (IObjectReference ref : refs) {
      LOGGER.debug("handling object reference " + ref.getField().getFullName());
      ref.getField().getPropertyMapper().fromObjectReference(tmpObject, ref, result -> {
        if (result.failed()) {
          co.setThrowable(result.cause());
          return;
        }
        if (co.reduce()) {
          LOGGER.debug("object references finished");
          handler.handle(Future.succeededFuture());
        }
      });

    }
  }

  /**
   * Initialize the internal entity into the StoreObject
   * 
   * @param handler
   */
  public void initFromEntity(Handler<AsyncResult<Void>> handler) {
    CounterObject<Void> co = new CounterObject<>(mapper.getFieldNames().size(), handler);
    for (String fieldName : mapper.getFieldNames()) {
      IField field = mapper.getField(fieldName);
      field.getPropertyMapper().intoStoreObject(entity, this, field, result -> {
        if (result.failed()) {
          co.setThrowable(result.cause());
        } else {
          if (co.reduce())
            handler.handle(Future.succeededFuture());
        }
      });
      if (co.isError()) {
        return;
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IStoreObject#getObjectReferences()
   */
  @Override
  public Collection<IObjectReference> getObjectReferences() {
    return objectReferences;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return mapper.getTableInfo().getName() + ": " + String.valueOf(requestMap);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IStoreObject#isNewInstance()
   */
  @Override
  public boolean isNewInstance() {
    return false;
  }

}

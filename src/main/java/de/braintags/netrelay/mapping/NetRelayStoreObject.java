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
import java.util.List;
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
import de.braintags.netrelay.NetRelay;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * NetRelayStoreObject is the bridge between http requests and mapper objects
 * 
 * @author Michael Remme
 * 
 * @param <T>
 *          the class of the mapper used
 */
public class NetRelayStoreObject<T> implements IStoreObject<T, Map<String, String>> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(NetRelayStoreObject.class);

  private IMapper<T> mapper;
  private T entity = null;
  private Collection<IObjectReference> objectReferences = new ArrayList<>();
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
  public NetRelayStoreObject(IMapper<T> mapper, T entity) {
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
   * @param entity
   *          the entity to be used
   * @param mapper
   *          the mapper to be used
   * @param netRelay
   *          the instance of NetRelay
   */
  public NetRelayStoreObject(Map<String, String> requestMap, T entity, IMapper<T> mapper, NetRelay netRelay) {
    Objects.requireNonNull(mapper, "Mapper must not be null");
    Objects.requireNonNull(requestMap, "requestMap must not be null");
    this.mapper = mapper;
    this.requestMap = requestMap;
    this.netRelay = netRelay;
    this.entity = entity;
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
  public IStoreObject<T, Map<String, String>> put(IField field, Object value) {
    requestMap.put(field.getName().toLowerCase(), String.valueOf(value));
    return this;
  }

  @Override
  public Map<String, String> getContainer() {
    return requestMap;
  }

  /**
   * @return the mapper
   */
  public IMapper<T> getMapper() {
    return mapper;
  }

  @Override
  public T getEntity() {
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
        T tmpObject = result.result();
        LOGGER.debug("start initToEntity");
        initToEntity(tmpObject, handler);
      }
    });
  }

  /**
   * @param tmpObject
   * @param handler
   */
  private void initToEntity(T tmpObject, Handler<AsyncResult<Void>> handler) {
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

  private void createEntity(Handler<AsyncResult<T>> handler) {
    if (entity != null) {
      handler.handle(Future.succeededFuture(entity));
    } else if (hasProperty(getMapper().getIdField())) {
<<<<<<< 378beb40fd3de1b6713ff2e2addcf387f4ea652f
      Object id = get(getMapper().getIdField());
      IQuery<T> query = netRelay.getDatastore().createQuery(getMapper().getMapperClass());
      query.setSearchCondition(query.isEqual(query.getMapper().getIdField().getName(), id));
      query.execute(qrr -> {
        if (qrr.failed()) {
          handler.handle(Future.failedFuture(qrr.cause()));
        } else {
          IQueryResult<T> qr = qrr.result();
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
=======
      queryEntity(handler);
>>>>>>> replacing CounterObject by CompositeFuture
    } else {
      T returnObject = getMapper().getObjectFactory().createInstance(getMapper().getMapperClass());
      handler.handle(Future.succeededFuture(returnObject));
    }
  }

  /**
   * Fetch the entity from datastore by executing a query
   * 
   * @param handler
   */
  private void queryEntity(Handler<AsyncResult<T>> handler) {
    Object id = get(getMapper().getIdField());
    IQuery<T> query = netRelay.getDatastore().createQuery(getMapper().getMapperClass());
    query.field(query.getMapper().getIdField().getName()).is(id);
    query.execute(qrr -> {
      if (qrr.failed()) {
        handler.handle(Future.failedFuture(qrr.cause()));
      } else {
        IQueryResult<T> qr = qrr.result();
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
  }

  protected void finishToEntity(T tmpObject, Handler<AsyncResult<Void>> handler) {
    this.entity = tmpObject;
    try {
      handler.handle(Future.succeededFuture());
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  /**
   * Iterate the fields if the mapper and - if a content exists in the current data -
   * add the new value into the entity
   * 
   * @param tmpObject
   * @param handler
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected final void iterateFields(T tmpObject, Handler<AsyncResult<Void>> handler) {
    LOGGER.debug("start iterateFields");
    Set<String> fieldNames = getMapper().getFieldNames();
    List<Future> fl = new ArrayList<>();
    for (String fieldName : fieldNames) {
      Future f = Future.future();
      fl.add(f);
      IField field = getMapper().getField(fieldName);
      if (hasProperty(field)) {
        LOGGER.debug("handling field " + field.getFullName());
        field.getPropertyMapper().fromStoreObject(tmpObject, this, field, f.completer());
      } else {
        f.complete();
      }
    }
    CompositeFuture cf = CompositeFuture.all(fl);
    cf.setHandler(cfr -> {
      if (cfr.failed()) {
        handler.handle(Future.failedFuture(cfr.cause()));
      } else {
        handler.handle(Future.succeededFuture());
      }
    });
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void iterateObjectReferences(Object tmpObject, Handler<AsyncResult<Void>> handler) {
    LOGGER.debug("start iterateObjectReferences");
    if (getObjectReferences().isEmpty()) {
      LOGGER.debug("nothing to do");
      handler.handle(Future.succeededFuture());
      return;
    }
    Collection<IObjectReference> refs = getObjectReferences();
    List<Future> fl = new ArrayList<>(refs.size());

    for (IObjectReference ref : refs) {
      LOGGER.debug("handling object reference " + ref.getField().getFullName());
      Future f = Future.future();
      fl.add(f);
      ref.getField().getPropertyMapper().fromObjectReference(tmpObject, ref, f.completer());
    }
    CompositeFuture cf = CompositeFuture.all(fl);
    cf.setHandler(cfr -> {
      if (cfr.failed()) {
        handler.handle(Future.failedFuture(cfr.cause()));
      } else {
        handler.handle(Future.succeededFuture());
      }
    });
  }

  /**
   * Initialize the internal entity into the StoreObject
   * 
   * @param handler
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void initFromEntity(Handler<AsyncResult<Void>> handler) {
    List<Future> fl = new ArrayList<>(mapper.getFieldNames().size());
    for (String fieldName : mapper.getFieldNames()) {
      IField field = mapper.getField(fieldName);
      Future f = Future.future();
      fl.add(f);
      field.getPropertyMapper().intoStoreObject(entity, this, field, f.completer());
    }
    CompositeFuture cf = CompositeFuture.all(fl);
    cf.setHandler(cfr -> {
      if (cfr.failed()) {
        handler.handle(Future.failedFuture(cfr.cause()));
      } else {
        handler.handle(Future.succeededFuture());
      }
    });
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
    return mapper.getTableInfo().getName() + ": " + requestMap;
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

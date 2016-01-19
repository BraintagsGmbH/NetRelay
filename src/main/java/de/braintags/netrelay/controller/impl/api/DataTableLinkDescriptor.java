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
package de.braintags.netrelay.controller.impl.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.util.CounterObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Contains all relevant parameters from a request which was sent to {@link DataTablesController}.
 * From those data the reply is processed
 * 
 * @author Michael Remme
 * 
 */
public class DataTableLinkDescriptor {
  private static final String ICOLUMNS = "iColumns";
  private static final String SCOLUMNS = "sColumns";
  private static final String DISPLAY_START = "iDisplayStart";
  private static final String DISPLAY_LENGTH = "iDisplayLength";

  private Class<?> mapperClass;
  private ColDef[] columns;
  private int displayStart;
  private int displayLength;

  /**
   * 
   */
  public DataTableLinkDescriptor(Class<?> mapperClass, RoutingContext context) {
    Objects.requireNonNull(mapperClass, "Mapper cass must not be null");
    this.mapperClass = mapperClass;
    extractColumns(context);
    extractStartLength(context);

  }

  /**
   * Generate an instance of IQuery from the information in here
   * 
   * @param dataStore
   * @return
   */
  public IQuery<?> toRecordsInTableQuery(IDataStore dataStore) {
    IQuery<?> query = dataStore.createQuery(mapperClass);
    return query;
  }

  /**
   * Generate an instance of IQuery from the information in here
   * 
   * @param dataStore
   *          the datastore to be used
   * @param mf
   *          the {@link IMapperFactory} which converts seach values
   * @return
   */
  public void toQuery(IDataStore dataStore, IMapperFactory mf, Handler<AsyncResult<IQuery<?>>> handler) {
    IQuery<?> query = dataStore.createQuery(mapperClass);
    query.setLimit(displayLength);
    query.setStart(displayStart);
    query.setReturnCompleteCount(true);
    List<ColDef> defs = clearColDefs();
    if (defs.size() <= 0) {
      querySuccess(query, handler);
    } else {
      loopColumns(query, defs, dataStore, mf, handler);
    }
  }

  private List<ColDef> clearColDefs() {
    ArrayList<ColDef> ret = new ArrayList<>();
    for (ColDef def : columns) {
      if (def != null && def.searchValue.hashCode() != 0) {
        ret.add(def);
      }
    }
    return ret;
  }

  private void loopColumns(IQuery<?> query, List<ColDef> defs, IDataStore dataStore, IMapperFactory mf,
      Handler<AsyncResult<IQuery<?>>> handler) {
    CounterObject<IQuery<?>> co = new CounterObject<>(defs.size(), handler);
    for (ColDef def : defs) {
      if (co.isError()) {
        break;
      }
      IField field = mf.getMapper(mapperClass).getField(def.name);
      ITypeHandler th = field.getTypeHandler();
      th.fromStore(def.searchValue, field, null, thResult -> {
        if (thResult.failed()) {
          co.setThrowable(thResult.cause());
        } else {
          Object value = thResult.result().getResult();
          if (allowContains(value)) {
            query.field(def.name).contains(value);
          } else {
            query.field(def.name).is(value);
          }
          if (co.reduce()) {
            querySuccess(query, handler);
          }
        }
      });
    }
  }

  private boolean allowContains(Object value) {
    return !value.getClass().isEnum();
  }

  private void querySuccess(IQuery<?> q, Handler<AsyncResult<IQuery<?>>> handler) {
    handler.handle(Future.succeededFuture(q));
  }

  // "iDisplayStart=0&iDisplayLength=10&"
  private void extractStartLength(RoutingContext context) {
    displayStart = Integer.parseInt(context.request().getParam(DISPLAY_START));
    displayLength = Integer.parseInt(context.request().getParam(DISPLAY_LENGTH));
  }

  // &iColumns=7&sColumns=id%2Cusername%2Cfirstname%2Clastname%2Cemail%2Cid%2C&"
  private void extractColumns(RoutingContext context) {
    String iCols = context.request().getParam(ICOLUMNS);
    String sCols = context.request().getParam(SCOLUMNS);
    int colCount = Integer.parseInt(iCols);
    String[] cols = sCols.split(",");

    columns = new ColDef[colCount];
    for (int i = 0; i < cols.length; i++) {
      columns[i] = new ColDef(context, cols[i], i);
    }
  }

  /**
   * @return the columns
   */
  public final ColDef[] getColumns() {
    return columns;
  }

  /**
   * @return the displayStart
   */
  public final int getDisplayStart() {
    return displayStart;
  }

  /**
   * @return the displayLength
   */
  public final int getDisplayLength() {
    return displayLength;
  }

  class ColDef {
    public String name;
    public String searchValue;

    ColDef(RoutingContext context, String name, int position) {
      this.name = name;
      extract(context, position);
    }

    // mDataProp_0=0&sSearch_0=11&bRegex_0=false&bSearchable_0=true&bSortable_0=true&
    void extract(RoutingContext context, int position) {
      searchValue = context.request().getParam("sSearch_" + position);

    }

  }

}

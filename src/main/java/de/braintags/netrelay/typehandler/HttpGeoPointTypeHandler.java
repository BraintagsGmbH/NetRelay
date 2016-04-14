/*
 * #%L
 * NetRelay
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.netrelay.typehandler;

import de.braintags.io.vertx.pojomapper.datatypes.geojson.GeoPoint;
import de.braintags.io.vertx.pojomapper.datatypes.geojson.Position;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * For http only the coordinates are sent and received as array
 * 
 * @author Michael Remme
 * 
 */
public class HttpGeoPointTypeHandler extends AbstractTypeHandler {

  /**
   * @param typeHandlerFactory
   */
  public HttpGeoPointTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, GeoPoint.class);
  }

  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : parse((String) source), resultHandler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : encode((GeoPoint) source), resultHandler);
  }

  private String encode(GeoPoint source) {
    return new JsonArray(source.getCoordinates().getValues()).encode();
  }

  private GeoPoint parse(String source) {
    JsonArray js = new JsonArray(source);
    Position pos = new Position(js.iterator());
    return new GeoPoint(pos);
  }

}

/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.netrelay.mapper;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class SimpleNetRelayMapper extends BaseRecord {
  public String name;
  public int age;
  public boolean child;
  public String fileName;
  public GeoPoint geoPoint;

}

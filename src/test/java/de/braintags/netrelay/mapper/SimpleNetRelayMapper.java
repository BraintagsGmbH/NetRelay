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

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.datatypes.geojson.GeoPoint;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;

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

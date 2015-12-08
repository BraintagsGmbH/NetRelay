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
package de.braintags.netrelay.init;

import java.util.HashMap;
import java.util.Map;

import de.braintags.netrelay.controller.IController;

/**
 * MappingDefinitions are functioning as a lookup, which mappers are used by the current server and which mapper
 * can be used by which reference name.
 * The definitions inside here can be predefined as part of the {@link Settings}. Additionally some instances of
 * {@link IController} can add further mapping definitions, id needed.
 * 
 * @author Michael Remme
 * 
 */
public class MappingDefinitions {
  private Map<String, Class> mapperMap = new HashMap();

  /**
   * The mappermap contains the key, by which a template for instance can refer to a mapper and the mapper class as
   * value
   *
   * @return the mapperMap
   */
  public Map<String, Class> getMapperMap() {
    return mapperMap;
  }

  /**
   * The mappermap contains the key, by which a template for instance can refer to a mapper and the mapper class as
   * value
   *
   * @param mapperMap
   *          the mapperMap to set
   */
  public void setMapperMap(Map<String, Class> mapperMap) {
    this.mapperMap = mapperMap;
  }

  public void addMapperDefinition(String name, Class mapperClass) {
    mapperMap.put(name.toLowerCase(), mapperClass);
  }

  public Class getMapperClass(String name) {
    return mapperMap.get(name.toLowerCase());
  }

}

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
package de.braintags.netrelay.routing;

import java.util.ArrayList;
import java.util.List;

import de.braintags.netrelay.init.Settings;

/**
 * RouterDefinitions is a part of the {@link Settings} and defines the existing list of {@link RouterDefinition}
 * 
 * @author Michael Remme
 * 
 */
public class RouterDefinitions {

  private List<RouterDefinition> routerDefinitions = new ArrayList<>();

  /**
   * Add a new definition at the end of the definitions
   * 
   * @param definition
   *          teh {@link RouterDefinition} to be added
   */
  public void add(RouterDefinition definition) {
    routerDefinitions.add(definition);
  }

  /**
   * Add a new definition at the end of the definitions
   * 
   * @param position
   *          the position where to add the new element
   * @param definition
   *          teh {@link RouterDefinition} to be added
   */
  public void add(int position, RouterDefinition definition) {
    routerDefinitions.add(position, definition);
  }

  /**
   * Add the given definition before one with the given name
   * 
   * @param definitionName
   *          the name of the definition, where before to add the new definition
   * @param def
   *          the definition to be added
   */
  public void addBefore(String definitionName, RouterDefinition def) {
    int position = getPosition(definitionName);
    if (position < 0) {
      throw new IndexOutOfBoundsException("Could not find definition with name '" + definitionName + "'");
    }
    routerDefinitions.add(position, def);
  }

  /**
   * Add the given definition before one with the given name
   * 
   * @param definitionName
   *          the name of the definition, where before to add the new definition
   * @param def
   *          the definition to be added
   */
  public void addAfter(String definitionName, RouterDefinition def) {
    int position = getPosition(definitionName);
    if (position < 0) {
      throw new IndexOutOfBoundsException("Could not find definition with name '" + definitionName + "'");
    }
    if (position + 1 > routerDefinitions.size()) {
      routerDefinitions.add(def);
    } else {
      routerDefinitions.add(position + 1, def);
    }
  }

  /**
   * @return the routerDefinitions
   */
  public final List<RouterDefinition> getRouterDefinitions() {
    return routerDefinitions;
  }

  /**
   * @param routerDefinitions
   *          the routerDefinitions to set
   */
  public final void setRouterDefinitions(List<RouterDefinition> routerDefinitions) {
    this.routerDefinitions = routerDefinitions;
  }

  /**
   * Remove the {@link RouterDefinition} with the specified name
   * 
   * @param defName
   *          the name of the definition to be removed
   * @return the removed {@link RouterDefinition} or null
   */
  public RouterDefinition remove(String defName) {
    int position = getPosition(defName);
    if (position >= 0) {
      return routerDefinitions.remove(position);
    }
    return null;
  }

  /**
   * Get the {@link RouterDefinition} with the specified name
   * 
   * @param name
   *          the name of the definition to search for
   * @return a definition with the given name or null
   */
  public RouterDefinition getNamedDefinition(String name) {
    for (RouterDefinition def : routerDefinitions) {
      if (def.getName().equals(name)) {
        return def;
      }
    }
    return null;
  }

  /**
   * Get the position of the definition with the given name.
   * 
   * @param name
   *          the name of the definition to search for
   * @return the position or -2 if not existing
   */
  public int getPosition(String name) {
    for (int i = 0; i < routerDefinitions.size(); i++) {
      RouterDefinition def = routerDefinitions.get(i);
      if (def.getName().equals(name)) {
        return i;
      }
    }
    return -2;
  }

}

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

import de.braintags.netrelay.init.Settings;

/**
 * TimerDefinitions is a part of the {@link Settings} and defines the existing list of {@link TimerDefinition}
 * 
 * @author Michael Remme
 * 
 */
public class TimerDefinitions {

  private ArrayList<TimerDefinition> timerDefinitions = new ArrayList<>();

  /**
   * Add a new definition at the end of the definitions
   * 
   * @param definition
   *          the {@link TimerDefinition} to be added
   */
  public void add(TimerDefinition definition) {
    timerDefinitions.add(definition);
  }

  /**
   * Add a new definition at the end of the definitions
   * 
   * @param position
   *          the position where to add the new element
   * @param definition
   *          the {@link TimerDefinition} to be added
   */
  public void add(int position, TimerDefinition definition) {
    timerDefinitions.add(position, definition);
  }

  /**
   * Add the given definition before one with the given name
   * 
   * @param definitionName
   *          the name of the definition, where before to add the new definition
   * @param def
   *          the definition to be added
   */
  public void addBefore(String definitionName, TimerDefinition def) {
    int position = getPosition(definitionName);
    if (position < 0) {
      throw new IndexOutOfBoundsException("Could not find definition with name '" + definitionName + "'");
    }
    timerDefinitions.add(position, def);
  }

  /**
   * Add the given definition before one with the given name
   * 
   * @param definitionName
   *          the name of the definition, where before to add the new definition
   * @param def
   *          the definition to be added
   */
  public void addAfter(String definitionName, TimerDefinition def) {
    int position = getPosition(definitionName);
    if (position < 0) {
      throw new IndexOutOfBoundsException("Could not find definition with name '" + definitionName + "'");
    }
    if (position + 1 > timerDefinitions.size()) {
      timerDefinitions.add(def);
    } else {
      timerDefinitions.add(position + 1, def);
    }
  }

  /**
   * @return the routerDefinitions
   */
  public final ArrayList<TimerDefinition> getTimerDefinitions() {
    return timerDefinitions;
  }

  /**
   * @param routerDefinitions
   *          the routerDefinitions to set
   */
  public final void setTimerDefinitions(ArrayList<TimerDefinition> timerDefinitions) {
    this.timerDefinitions = timerDefinitions;
  }

  /**
   * Remove the {@link TimerDefinition} with the specified name
   * 
   * @param defName
   *          the name of the definition to be removed
   * @return the removed {@link TimerDefinition} or null
   */
  public TimerDefinition remove(String defName) {
    int position = getPosition(defName);
    if (position >= 0) {
      return timerDefinitions.remove(position);
    }
    return null;
  }

  /**
   * Get the {@link TimerDefinition} with the specified name
   * 
   * @param name
   *          the name of the definition to search for
   * @return a definition with the given name or null
   */
  public TimerDefinition getNamedDefinition(String name) {
    for (TimerDefinition def : timerDefinitions) {
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
    for (int i = 0; i < timerDefinitions.size(); i++) {
      TimerDefinition def = timerDefinitions.get(i);
      if (def.getName().equals(name)) {
        return i;
      }
    }
    return -2;
  }

}

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
package de.braintags.netrelay.processor;

import java.util.ArrayList;

import de.braintags.netrelay.init.Settings;

/**
 * ProcessorDefinitions is a part of the {@link Settings} and defines the existing list of {@link ProcessorDefinition}
 * 
 * @author Michael Remme
 * 
 */
public class ProcessorDefinitions {

  private ArrayList<ProcessorDefinition> processorDefinitions = new ArrayList<>();

  /**
   * Add a new definition at the end of the definitions
   * 
   * @param definition
   *          the {@link ProcessorDefinition} to be added
   */
  public void add(ProcessorDefinition definition) {
    processorDefinitions.add(definition);
  }

  /**
   * Add a new definition at the end of the definitions
   * 
   * @param position
   *          the position where to add the new element
   * @param definition
   *          the {@link ProcessorDefinition} to be added
   */
  public void add(int position, ProcessorDefinition definition) {
    processorDefinitions.add(position, definition);
  }

  /**
   * Add the given definition before one with the given name
   * 
   * @param definitionName
   *          the name of the definition, where before to add the new definition
   * @param def
   *          the definition to be added
   */
  public void addBefore(String definitionName, ProcessorDefinition def) {
    int position = getPosition(definitionName);
    if (position < 0) {
      throw new IndexOutOfBoundsException("Could not find definition with name '" + definitionName + "'");
    }
    processorDefinitions.add(position, def);
  }

  /**
   * Add the given definition before one with the given name
   * 
   * @param definitionName
   *          the name of the definition, where before to add the new definition
   * @param def
   *          the definition to be added
   */
  public void addAfter(String definitionName, ProcessorDefinition def) {
    int position = getPosition(definitionName);
    if (position < 0) {
      throw new IndexOutOfBoundsException("Could not find definition with name '" + definitionName + "'");
    }
    if (position + 1 > processorDefinitions.size()) {
      processorDefinitions.add(def);
    } else {
      processorDefinitions.add(position + 1, def);
    }
  }

  /**
   * The list of {@link ProcessorDefinition}
   * 
   * @return the processorDefinitions
   */
  public final ArrayList<ProcessorDefinition> getProcessorDefinitions() {
    return processorDefinitions;
  }

  /**
   * The list of {@link ProcessorDefinition}
   * 
   * @param processorDefinitions
   *          the processorDefinitions to set
   */
  public final void setProcessorDefinitions(ArrayList<ProcessorDefinition> processorDefinitions) {
    this.processorDefinitions = processorDefinitions;
  }

  /**
   * Remove the {@link ProcessorDefinition} with the specified name
   * 
   * @param defName
   *          the name of the definition to be removed
   * @return the removed {@link ProcessorDefinition} or null
   */
  public ProcessorDefinition remove(String defName) {
    int position = getPosition(defName);
    if (position >= 0) {
      return processorDefinitions.remove(position);
    }
    return null;
  }

  /**
   * Get the {@link ProcessorDefinition} with the specified name
   * 
   * @param name
   *          the name of the definition to search for
   * @return a definition with the given name or null
   */
  public ProcessorDefinition getNamedDefinition(String name) {
    for (ProcessorDefinition def : processorDefinitions) {
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
    for (int i = 0; i < processorDefinitions.size(); i++) {
      ProcessorDefinition def = processorDefinitions.get(i);
      if (def.getName().equals(name)) {
        return i;
      }
    }
    return -2;
  }

}

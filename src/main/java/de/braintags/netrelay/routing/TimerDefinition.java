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
package de.braintags.netrelay.routing;

import java.util.Properties;

import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.processor.IProcessor;
import io.vertx.core.Vertx;

/**
 * A TimerDefinition defines, which {@link IProcessor} shall be periodically executed
 * 
 * Currently the timeDef supports the following definitions:
 * <UL>
 * <LI>1000 = 1000 ms
 * 
 * </UL>
 * 
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TimerDefinition {
  private boolean cancelOnError = false;
  private String timeDef;
  protected String name;
  private boolean active = true;
  private Class<? extends IProcessor> processor;
  private Properties processorProperties = new Properties();

  /**
   * Initialize the given definition, so that it is executed regular like planned
   * 
   * @param vertx
   *          the instance of vertx to be used
   * @param netRelay
   *          the instance of NetRelay to be used
   */
  public void initTimerDefinition(Vertx vertx, NetRelay netRelay) {
    // IProcessor processor = instantiateProcessor(vertx, netRelay);

  }

  /**
   * Create an instance of the defined IProcessor and init it with the defined properties
   * 
   * @return the intialized IProcessor
   */
  public IProcessor instantiateProcessor(Vertx vertx, NetRelay netRelay) throws Exception {
    IProcessor processor = getProcessor().newInstance();
    processor.init(vertx, netRelay, getProcessorProperties(), name);
    return processor;
  }

  /**
   * Defines, whether a timer shall be completely finished, if an error occured
   * 
   * @return true, if timer shall be finished
   */
  public final boolean isCancelOnError() {
    return cancelOnError;
  }

  /**
   * Defines, whether a timer shall be completely finished, if an error occured
   * 
   * @param cancelOnError
   *          the cancelOnError to set
   */
  public final void setCancelOnError(boolean cancelOnError) {
    this.cancelOnError = cancelOnError;
  }

  /**
   * The definition of the time periode(s), where the timer shall be executed
   * 
   * @return the timeDef
   */
  public final String getTimeDef() {
    return timeDef;
  }

  /**
   * The definition of the time periode(s), where the timer shall be executed
   * 
   * @param timeDef
   *          the timeDef to set
   */
  public final void setTimeDef(String timeDef) {
    this.timeDef = timeDef;
  }

  /**
   * Defines properties, by which an {@link IProcessor} is initialized
   * 
   * @return the handlerProperties
   */
  public final Properties getProcessorProperties() {
    return processorProperties;
  }

  /**
   * Defines properties, by which an {@link IProcessor} is initialized. Which properties are possible to be defined
   * here, is described inside the appropriate implementation of IProcessor
   * 
   * @param handlerProperties
   *          the handlerProperties to set.
   */
  public final void setProcessorProperties(Properties processorProperties) {
    this.processorProperties = processorProperties;
  }

  /**
   * Defines properties, by which an {@link IProcessor} is initialized. Which properties are possible to be defined
   * here, is described inside the appropriate implementation of IProcessor
   * 
   * @return the defined name
   */
  public String getName() {
    return name;
  }

  /**
   * The name of the definition is used for display
   * 
   * @param name
   *          the name to set
   */
  public final void setName(String name) {
    this.name = name;
  }

  /**
   * Get the {@link IProcessor} which shall be executed
   * 
   * @return the processor
   */
  public Class<? extends IProcessor> getProcessor() {
    return processor;
  }

  /**
   * Set the {@link IProcessor} which shall be executed
   * 
   * @param processor
   *          the processor to set
   */
  public final void setProcessor(Class<? extends IProcessor> processor) {
    this.processor = processor;
  }

  /**
   * @return the active
   */
  public final boolean isActive() {
    return active;
  }

  /**
   * @param active
   *          the active to set
   */
  public final void setActive(boolean active) {
    this.active = active;
  }
}

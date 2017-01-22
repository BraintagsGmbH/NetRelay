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
package de.braintags.netrelay.processor;

import java.util.Properties;

import de.braintags.vertx.util.ExceptionUtil;
import de.braintags.netrelay.NetRelay;
import io.vertx.core.Vertx;

/**
 * A ProcessorDefinition defines, which {@link IProcessor} shall be periodically executed
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
public class ProcessorDefinition {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(ProcessorDefinition.class);

  protected String name;
  private Class<? extends IProcessor> processorClass;
  private String timeDef;
  private boolean active = true;
  private boolean cancelOnError = false;
  private Properties processorProperties = new Properties();

  private transient IProcessor processor;

  /**
   * Create an instance of the defined IProcessor and init it with the current definition
   * 
   * @param vertx
   *          the instance of vertx to be used
   * @param netRelay
   *          the instance of NetRelay to be used
   */
  public void initProcessorDefinition(Vertx vertx, NetRelay netRelay) {
    try {
      if (active) {
        LOGGER.info("initializing processor " + getName() + " | " + getProcessorClass().getName());
        processor = getProcessorClass().newInstance();
        processor.init(vertx, netRelay, this);
      }
    } catch (Exception e) {
      throw ExceptionUtil.createRuntimeException(e);
    }
  }

  /**
   * Defines, whether a processorClass shall be completely finished, if an error occured
   * 
   * @return true, if processorClass shall be finished on an error
   */
  public final boolean isCancelOnError() {
    return cancelOnError;
  }

  /**
   * Defines, whether a processorClass shall be completely finished, if an error occured
   * 
   * @param cancelOnError
   *          the cancelOnError to set
   */
  public final void setCancelOnError(boolean cancelOnError) {
    this.cancelOnError = cancelOnError;
  }

  /**
   * The definition of the time periode(s), where the processorClass shall be executed
   * 
   * @return the timeDef
   */
  public final String getTimeDef() {
    return timeDef;
  }

  /**
   * The definition of the time periode(s), where the processorClass shall be executed
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
   * @return the processorClass
   */
  public Class<? extends IProcessor> getProcessorClass() {
    return processorClass;
  }

  /**
   * Set the {@link IProcessor} which shall be executed
   * 
   * @param processorClass
   *          the processorClass to set
   */
  public final void setProcessorClass(Class<? extends IProcessor> processor) {
    this.processorClass = processor;
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

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

import de.braintags.netrelay.TProcessorDefs;
import de.braintags.netrelay.processor.impl.AbstractProcessor;
import io.vertx.core.Future;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class DemoProcessor extends AbstractProcessor {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(DemoProcessor.class);

  public static final String DEMO_PROPERTY_KEY = "demoKey";

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.processor.impl.AbstractProcessor#internalInit(de.braintags.netrelay.processor.
   * ProcessorDefinition)
   */
  @Override
  protected void internalInit(ProcessorDefinition def) {
    TProcessorDefs.demoProperty = def.getProcessorProperties().getProperty(DEMO_PROPERTY_KEY);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.processor.impl.AbstractProcessor#handleEvent()
   */
  @Override
  protected void handleEvent(Future<Void> future) {
    LOGGER.info("executing processor");
    TProcessorDefs.eventProcessed = true;
    future.complete();
  }

}

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
package de.braintags.netrelay.processor.impl;

import de.braintags.netrelay.processor.IProcessor;
import de.braintags.netrelay.processor.ProcessorDefinition;
import io.vertx.core.Future;

/**
 * An abstract implementation of an {@link IProcessor}, which is used to send emails, where the content is created by a
 * template
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractMailProcessor extends AbstractProcessor {

  /**
   * 
   */
  public AbstractMailProcessor() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.processor.impl.AbstractProcessor#internalInit(de.braintags.netrelay.processor.
   * ProcessorDefinition)
   */
  @Override
  protected void internalInit(ProcessorDefinition def) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.processor.impl.AbstractProcessor#handleEvent(io.vertx.core.Future)
   */
  @Override
  protected void handleEvent(Future<Void> future) {
  }

}

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

import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.processor.IProcessor;
import de.braintags.netrelay.processor.ProcessorDefinition;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * An abstract implementation of {@link IProcessor}
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractProcessor implements IProcessor {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractProcessor.class);

  protected NetRelay netRelay;
  protected Vertx vertx;
  /**
   * The id of a running processor
   */
  protected Long timerId;
  private boolean finishOnError = false;

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(Long timerId) {
    this.timerId = timerId;
    Future<Void> future = Future.future();
    future.setHandler(ar -> {
      if (ar.failed()) {
        if (finishOnError) {
          LOGGER.warn("Finishing processor " + getClass().getName() + " cause of an error", ar.cause());
          vertx.cancelTimer(timerId);
        } else {
          LOGGER.warn("Error occured in processor " + getClass().getName(), ar.cause());
        }
      } else {
        // nothing to do at the time
      }
    });
    handleEvent(future);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.processor.IProcessor#init(io.vertx.core.Vertx, de.braintags.netrelay.NetRelay,
   * de.braintags.netrelay.processor.ProcessorDefinition)
   */
  @Override
  public final void init(Vertx vertx, NetRelay netRelay, ProcessorDefinition def) {
    this.vertx = vertx;
    this.netRelay = netRelay;
    internalInit(def);
    vertx.setPeriodic(Long.parseLong(def.getTimeDef()), this);
  }

  /**
   * Initialize the processor by using the information from the {@link ProcessorDefinition}
   * 
   * @param def
   */
  protected abstract void internalInit(ProcessorDefinition def);

  /**
   * implement the execution of the task, which the processor shall fulfill
   * 
   * @param future
   *          the Future to be informed about an occured error or success
   */
  protected abstract void handleEvent(Future<Void> future);
}

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

import de.braintags.netrelay.NetRelay;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * An IProcessor is a unit, which executes some actions in a time based manner
 * 
 * @author Michael Remme
 * 
 */
public interface IProcessor extends Handler<Long> {

  /**
   * Initializes the processor by using the given {@link ProcessorDefinition} and launches the processor as timer or
   * periodic timer
   * 
   * @param vertx
   *          the instance of {@link Vertx} to be used
   * @param netRelay
   *          the parent {@link NetRelay} to be used
   * @param def
   *          the definition, by which the behaviour of the processor is defined
   */
  void init(Vertx vertx, NetRelay netRelay, ProcessorDefinition def);

}

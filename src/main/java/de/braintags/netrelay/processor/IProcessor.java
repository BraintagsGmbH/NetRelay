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

import de.braintags.netrelay.NetRelay;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public interface IProcessor extends Handler<Long> {

  /**
   * @param vertx
   * @param netRelay
   * @param processorProperties
   * @param name
   */
  void init(Vertx vertx, NetRelay netRelay, Properties processorProperties, String name);

}

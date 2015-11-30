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
package de.braintags.netrelay.impl;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.init.Settings;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class NetRelayExtended extends NetRelay {

  /**
   * 
   */
  public NetRelayExtended() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelay#initDataStore()
   */
  @Override
  public IDataStore initDataStore() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelay#createDefaultSettings()
   */
  @Override
  public Settings createDefaultSettings() {
    return super.createDefaultSettings();
  }

}

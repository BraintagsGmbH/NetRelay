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

import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.init.Settings;

/**
 * An extension of NetRelay which is loading the Settings from a file
 * 
 * @author Michael Remme
 * 
 */
public class NetRelayExt_FileBasedSettings extends NetRelay {

  /**
   * 
   */
  public NetRelayExt_FileBasedSettings() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelay#createDefaultSettings()
   */
  @Override
  public Settings createDefaultSettings() {
    Settings settings = super.createDefaultSettings();
    settings.getDatastoreSettings().setDatabaseName("NetRelayExtended_DB");
    return settings;
  }

}
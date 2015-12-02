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
 * An extension of NetRelay which is getting the {@link Settings} from external
 * 
 * @author Michael Remme
 * 
 */
public class NetRelayExt_InternalSettings extends NetRelay {
  private Settings settings;
  private boolean settingsLocked = false;

  /**
   * 
   */
  public NetRelayExt_InternalSettings() {
    settings = createDefaultSettings();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelay#initSettings()
   */
  @Override
  protected Settings initSettings() {
    settingsLocked = true;
    return settings;
  }

  /**
   * For test units to be able to modify settings programmatically
   * 
   * @return
   */
  public Settings getSettings() {
    if (settingsLocked)
      throw new IllegalArgumentException("Settings were used already, no more possible to modify external");
    return settings;
  }
}

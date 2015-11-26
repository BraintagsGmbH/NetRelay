/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.netrelay;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.braintags.netrelay.impl.NetRelayExtended;
import io.vertx.ext.unit.junit.VertxUnitRunner;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@RunWith(VertxUnitRunner.class)
public class TestSettings {

  /**
   * 
   */
  public TestSettings() {
  }

  /**
   * creates new Settings and stores them inside the local user directory
   */
  @Test
  public void testInitSettingsNewInUserDir() {
    NetRelayExtended nr = new NetRelayExtended();

  }

}

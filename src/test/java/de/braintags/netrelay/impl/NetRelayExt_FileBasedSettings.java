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

import de.braintags.io.vertx.pojomapper.mongo.init.MongoDataStoreInit;
import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.mapper.SimpleNetRelayMapper;
import de.braintags.netrelay.unit.NetRelayBaseTest;

/**
 * An extension of NetRelay which is loading the Settings from a file
 * 
 * @author Michael Remme
 * 
 */
public class NetRelayExt_FileBasedSettings extends NetRelay {
  public static final String SIMPLEMAPPER_NAME = "SimpleNetRelayMapper";
  private boolean settingsEdited = false;

  public NetRelayExt_FileBasedSettings() {
  }

  /**
   * 
   */
  public NetRelayExt_FileBasedSettings(boolean settingsEdited) {
    this.settingsEdited = settingsEdited;
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
    settings.getMappingDefinitions().addMapperDefinition(SIMPLEMAPPER_NAME, SimpleNetRelayMapper.class);
    return settings;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelay#initSettings()
   */
  @Override
  protected Settings initSettings() {
    Settings settings = null;
    if (settingsEdited) {
      settings = initSettingsWithoutEditCheck();
    } else {
      settings = super.initSettings();
    }
    if (settingsEdited) {
      settings.setEdited(true);
    }
    applySystemProperties(settings);
    return settings;
  }

  public static void applySystemProperties(Settings settings) {
    String connectionString = System.getProperty(MongoDataStoreInit.CONNECTION_STRING_PROPERTY, null);
    if (connectionString != null) {
      settings.getDatastoreSettings().getProperties().put(MongoDataStoreInit.CONNECTION_STRING_PROPERTY,
          connectionString);
    }
    String sl = System.getProperty(MongoDataStoreInit.START_MONGO_LOCAL_PROP, null);
    if (sl != null) {
      settings.getDatastoreSettings().getProperties().put(MongoDataStoreInit.START_MONGO_LOCAL_PROP, sl);
    }
    String localPort = System.getProperty(MongoDataStoreInit.LOCAL_PORT_PROP, null);
    if (localPort != null) {
      settings.getDatastoreSettings().getProperties().put(MongoDataStoreInit.LOCAL_PORT_PROP, localPort);
    }

    String portString = System.getProperty(NetRelayBaseTest.SERVER_PORT_PROPERTY, null);
    if (portString != null) {
      settings.setServerPort(Integer.parseInt(portString));
    }
  }

  protected Settings initSettingsWithoutEditCheck() {
    try {
      Settings settings = Settings.loadSettings(this, vertx, context);
      return settings;
    } catch (Exception e) {
      throw e;
    }
  }

}

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
package de.braintags.netrelay.init;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.init.DataStoreSettings;
import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.FileSystemException;
import io.vertx.core.json.Json;

/**
 * The settings contain the preferences to launch NetRelay and are stored locally as file. They are loaded / created on
 * startup following the rules:
 * 
 * checking wether there exists a defined location inside the {@link Context} under the property
 * {@link #SETTINGS_LOCATION_PROPERTY}. If so, then the data are loaded from that file.
 * 
 * checking wether there exists a file named "nrSettings.json" in the directory ".netrelay" inside the user directory
 * If so, then it is loaded.
 * 
 * If it does not exist, a default instance is created and saved in the user directory in the subdirectory ".netrelay"
 * 
 * 
 * @author Michael Remme
 * 
 */
public class Settings {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(Settings.class);

  /**
   * The property which can be used to set the location of the stored file with Settings information
   */
  public static final String SETTINGS_LOCATION_PROPERTY = "de.braintags.netrelay.settings.path";

  /**
   * The local directory for NetRelay
   */
  public static final String LOCAL_USER_DIRECTORY = System.getProperty("user.home") + "/" + ".netrelay";

  private int serverPort = 8080;
  private List<RouterDefinition> routerDefinitions = new ArrayList<>();
  private DataStoreSettings datastoreSettings;
  private boolean edited = false;

  /**
   * 
   */
  public Settings() {
  }

  /**
   * @return the serverPort
   */
  public final int getServerPort() {
    return serverPort;
  }

  /**
   * @param serverPort
   *          the serverPort to set
   */
  public final void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }

  /**
   * Get the {@link RouterDefinition} with the specified name
   * 
   * @param name
   *          the name of the definition to search for
   * @return a definition with the given name or null
   */
  public RouterDefinition getNamedDefinition(String name) {
    for (RouterDefinition def : routerDefinitions) {
      if (def.getName().equals(name)) {
        return def;
      }
    }
    return null;
  }

  /**
   * @return the routerDefinitions
   */
  public final List<RouterDefinition> getRouterDefinitions() {
    return routerDefinitions;
  }

  /**
   * @param routerDefinitions
   *          the routerDefinitions to set
   */
  public final void setRouterDefinitions(List<RouterDefinition> routerDefinitions) {
    this.routerDefinitions = routerDefinitions;
  }

  /**
   * The {@link DataStoreSettings} by which the IDataStore used by the current application is initialized
   * 
   * @return the datastoreSettings
   */
  public final DataStoreSettings getDatastoreSettings() {
    return datastoreSettings;
  }

  /**
   * The {@link DataStoreSettings} by which the IDataStore used by the current application is initialized
   * 
   * @param datastoreSettings
   *          the datastoreSettings to set
   */
  public final void setDatastoreSettings(DataStoreSettings datastoreSettings) {
    this.datastoreSettings = datastoreSettings;
  }

  /**
   * Loads existing settings from the context, when the property {@link #SETTINGS_LOCATION_PROPERTY} is defined;
   * or loads or generates default settings and stores them in the local user directory, subdirectory .netrelay
   * 
   * @param netRelay
   *          the instance of NetRelay, which would create the default settings
   * @param vertx
   *          the instance of {@link Vertx} to be used
   * @param context
   *          the context, which could contain the property {@link #SETTINGS_LOCATION_PROPERTY}, where the location of
   *          the settings file is defined
   * @return
   */
  public static Settings loadSettings(NetRelay netRelay, Vertx vertx, Context context) {
    String path = context.config().getString(SETTINGS_LOCATION_PROPERTY);
    if (path != null) {
      return loadSettings(netRelay, vertx, path);
    } else {
      vertx.fileSystem().mkdirsBlocking(LOCAL_USER_DIRECTORY);
      String localSettingsFileName = LOCAL_USER_DIRECTORY + "/" + netRelay.getClass().getName() + ".settings.json";
      return loadSettings(netRelay, vertx, localSettingsFileName);
    }
  }

  private static Settings loadSettings(NetRelay netRelay, Vertx vertx, String path) {
    FileSystem fs = vertx.fileSystem();
    if (fs.existsBlocking(path)) {
      LOGGER.debug("going to load settings from " + path);
      Buffer buffer = fs.readFileBlocking(path);
      Settings settings = Json.decodeValue(buffer.toString(), Settings.class);
      LOGGER.debug("settings successfully loaded from " + path);
      return settings;
    } else {
      LOGGER.debug("creating default settings and store them in " + path);
      Settings settings = netRelay.createDefaultSettings();
      fs.writeFileBlocking(path, Buffer.buffer(Json.encode(settings)));
      throw new FileSystemException("File did not exist and was created new in path " + path);
    }

  }

  /**
   * If settings are autogenerated with the default values, they must be edited to apply them to the needs of the
   * project.
   * As long as this value is false ( which is the default value for new Settings ), the system won't start and cancel
   * the launch.
   * 
   * @return false, as long as value wasn't changed manually
   */
  public final boolean isEdited() {
    return edited;
  }

  /**
   * If settings are autogenerated with the default values, they must be edited to apply them to the needs of the
   * project.
   * As long as this value is false ( which is the default value for new Settings ), the system won't start and cancel
   * the launch.
   * 
   * @param edited
   *          set it to true, so that system is able to launch
   */
  public final void setEdited(boolean edited) {
    this.edited = edited;
  }

}

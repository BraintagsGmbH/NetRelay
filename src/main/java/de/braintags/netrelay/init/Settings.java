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

import de.braintags.io.vertx.pojomapper.init.DataStoreSettings;
import de.braintags.io.vertx.util.exception.InitException;
import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.processor.ProcessorDefinitions;
import de.braintags.netrelay.routing.RouterDefinitions;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.Json;
import io.vertx.ext.mail.MailClient;

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
  private String hostName = "localhost";
  private boolean edited = false;
  private String defaultLoginPage = "/login.html";
  private DataStoreSettings datastoreSettings;
  private RouterDefinitions routerDefinitions = new RouterDefinitions();
  private ProcessorDefinitions processorDefinitons = new ProcessorDefinitions();
  private MailClientSettings mailClientSettings = new MailClientSettings();
  private MappingDefinitions mappingDefinitions = new MappingDefinitions();
  private int sslPort = -1;
  private boolean certificateSelfSigned = false;
  private String certificatePassword;
  private String certificatePath;
  private String certificateKeyPath;

  /**
   * The port, where the server shall run on
   * 
   * @return the serverPort
   */
  public final int getServerPort() {
    return serverPort;
  }

  /**
   * The port, where the server shall run on
   * 
   * @param serverPort
   *          the serverPort to set
   */
  public final void setServerPort(int serverPort) {
    if (serverPort <= 0) {
      throw new IllegalArgumentException("Port must be > 0");
    }
    this.serverPort = serverPort;
  }

  /**
   * the {@link RouterDefinitions} for the current settings
   * 
   * @return the routerDefinitions
   */
  public final RouterDefinitions getRouterDefinitions() {
    return routerDefinitions;
  }

  /**
   * the {@link RouterDefinitions} for the current settings
   * 
   * @param routerDefinitions
   *          the routerDefinitions to set
   */
  public final void setRouterDefinitions(RouterDefinitions routerDefinitions) {
    this.routerDefinitions = routerDefinitions;
  }

  /**
   * The {@link ProcessorDefinitions} of the current settings
   * 
   * @return the processorDefinitons
   */
  public final ProcessorDefinitions getProcessorDefinitons() {
    return processorDefinitons;
  }

  /**
   * The {@link ProcessorDefinitions} of the current settings
   * 
   * @param processorDefinitons
   *          the processorDefinitons to set
   */
  public final void setProcessorDefinitons(ProcessorDefinitions processorDefinitons) {
    this.processorDefinitons = processorDefinitons;
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
      LOGGER.info("going to load settings from " + path);
      Buffer buffer = fs.readFileBlocking(path);
      Settings settings = Json.decodeValue(buffer.toString(), Settings.class);
      LOGGER.info("settings successfully loaded from " + path);
      return settings;
    } else {
      LOGGER.info("creating default settings and store them in " + path);
      Settings settings = netRelay.createDefaultSettings();
      fs.writeFileBlocking(path, Buffer.buffer(Json.encodePrettily(settings)));
      String message = String.format(
          "Settings file did not exist and was created new in path %s. NOTE: edit the file, set edited to true and restart the server",
          path);
      throw new InitException(message);
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

  /**
   * The {@link MappingDefinitions} used by this instance
   * 
   * @return the mappingDefinitions
   */
  public MappingDefinitions getMappingDefinitions() {
    return mappingDefinitions;
  }

  /**
   * The {@link MappingDefinitions} used by this instance
   * 
   * @param mappingDefinitions
   *          the mappingDefinitions to set
   */
  public void setMappingDefinitions(MappingDefinitions mappingDefinitions) {
    this.mappingDefinitions = mappingDefinitions;
  }

  /**
   * The settings, by which the internally used {@link MailClient} is initialized
   * 
   * @return the mailClientSettings
   */
  public final MailClientSettings getMailClientSettings() {
    return mailClientSettings;
  }

  /**
   * The settings, by which the internally used {@link MailClient} is initialized
   * 
   * @param mailClientSettings
   *          the mailClientSettings to set
   */
  public final void setMailClientSettings(MailClientSettings mailClientSettings) {
    this.mailClientSettings = mailClientSettings;
  }

  /**
   * The definition of the default page, which is used for a login redirect
   *
   * @return the defaultLoginPage
   */
  public final String getDefaultLoginPage() {
    return defaultLoginPage;
  }

  /**
   * The definition of the default page, which is used for a login redirect
   * 
   * @param defaultLoginPage
   *          the defaultLoginPage to set
   */
  public final void setDefaultLoginPage(String defaultLoginPage) {
    this.defaultLoginPage = defaultLoginPage;
  }

  /**
   * The name of the host
   * 
   * @return the hostName
   */
  public final String getHostName() {
    return hostName;
  }

  /**
   * The name of the host
   * 
   * @param hostName
   *          the hostName to set
   */
  public final void setHostName(String hostName) {
    this.hostName = hostName;
  }

  /**
   * The ssl port, where the server shall listen to. If the port is <= 0, no ssl server is started
   * 
   * @return the sslPort
   */
  public int getSslPort() {
    return sslPort;
  }

  /**
   * The ssl port, where the server shall listen to. If the port is <= 0, no ssl server is started
   * 
   * @param sslPort
   *          the sslPort to set
   */
  public void setSslPort(int sslPort) {
    this.sslPort = sslPort;
  }

  /**
   * Shall the system generate a certificate?
   * 
   * @return the certificateSelfSigned
   */
  public boolean isCertificateSelfSigned() {
    return certificateSelfSigned;
  }

  /**
   * Shall the system generate a certificate?
   * 
   * @param certificateSelfSigned
   *          the certificateSelfSigned to set
   */
  public void setCertificateSelfSigned(boolean certificateSelfSigned) {
    this.certificateSelfSigned = certificateSelfSigned;
  }

  /**
   * The password which shall be used for a certificate
   * 
   * @return the certificatePassword
   */
  public String getCertificatePassword() {
    return certificatePassword;
  }

  /**
   * The password which shall be used for a certificate
   * 
   * @param certificatePassword
   *          the certificatePassword to set
   */
  public void setCertificatePassword(String certificatePassword) {
    this.certificatePassword = certificatePassword;
  }

  /**
   * The path to the certificate file as PEM file
   * 
   * @return the certificatePath
   */
  public String getCertificatePath() {
    return certificatePath;
  }

  /**
   * The path to the certificate file as PEM file
   * 
   * @param certificatePath
   *          the certificatePath to set
   */
  public void setCertificatePath(String certificatePath) {
    this.certificatePath = certificatePath;
  }

  /**
   * The path to the key file of a certificate
   * 
   * @return the certificateKeyPath
   */
  public String getCertificateKeyPath() {
    return certificateKeyPath;
  }

  /**
   * The path to the key file of a certificate
   * 
   * @param certificateKeyPath
   *          the certificateKeyPath to set
   */
  public void setCertificateKeyPath(String certificateKeyPath) {
    this.certificateKeyPath = certificateKeyPath;
  }

}

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
package de.braintags.netrelay;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.braintags.io.vertx.pojomapper.testdatastore.TestHelper;
import de.braintags.netrelay.impl.NetRelayExtended;
import de.braintags.netrelay.init.Settings;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@RunWith(VertxUnitRunner.class)
public class TestSettings {
  private static String localSettingsFileNameUserDir = Settings.LOCAL_USER_DIRECTORY + "/"
      + NetRelayExtended.class.getName() + ".settings.json";
  private static String localSettingsFileNameTmpDir = System.getProperty("java.io.tmpdir") + "/"
      + NetRelayExtended.class.getName() + ".settings.json";
  private static Vertx vertx = Vertx.vertx(TestHelper.getOptions());

  /**
   * creates new Settings and stores them inside the local user directory
   */
  @Test
  public void testInitSettingsNewInUserDir(TestContext context) {

    context.remove(Settings.SETTINGS_LOCATION_PROPERTY);
    context.assertNotNull(vertx);
    FileSystem fs = vertx.fileSystem();
    deleteFileInDir(context, fs, localSettingsFileNameUserDir);

    Async async = context.async();
    vertx.deployVerticle(NetRelayExtended.class.getName(), result -> {
      try {
        if (result.failed()) {
          context.assertTrue(fs.existsBlocking(localSettingsFileNameUserDir),
              "does not exist: " + localSettingsFileNameUserDir);
        } else {
          context.fail("This test expects to fail, cause the settings were created new on disc");
        }
      } finally {
        async.complete();
      }
    });

    async.awaitSuccess();

    final Async async2 = context.async();
    vertx.deployVerticle(NetRelayExtended.class.getName(), result -> {
      if (result.failed()) {
        context.fail(result.cause());
      } else {
        context.assertTrue(fs.existsBlocking(localSettingsFileNameUserDir));
        async2.complete();
      }
    });
  }

  /**
   * Check for a file inside another location.
   * 
   */
  @Test
  public void testInitSettingsFromOtherDir(TestContext context) {
    context.assertNotNull(vertx);
    FileSystem fs = vertx.fileSystem();
    Settings setting = new NetRelayExtended().createDefaultSettings();

    deleteFileInDir(context, fs, localSettingsFileNameTmpDir);
    Async async = context.async();

    DeploymentOptions options = new DeploymentOptions();
    options.setConfig(new JsonObject().put(Settings.SETTINGS_LOCATION_PROPERTY, localSettingsFileNameTmpDir));
    vertx.deployVerticle(NetRelayExtended.class.getName(), options, result -> {
      try {
        if (result.failed()) {
          context.assertTrue(fs.existsBlocking(localSettingsFileNameUserDir),
              "does not exist: " + localSettingsFileNameUserDir);
        } else {
          context.fail("This test expects to fail, cause the settings were created new on disc");
        }
      } finally {
        async.complete();
      }
    });
    async.awaitSuccess(2000);

    final Async async2 = context.async();
    vertx.deployVerticle(NetRelayExtended.class.getName(), options, result -> {
      if (result.failed()) {
        context.fail(result.cause());
      } else {
        context.assertTrue(fs.existsBlocking(localSettingsFileNameUserDir));
        async2.complete();
      }
    });
  }

  private void deleteFileInDir(TestContext context, FileSystem fs, String path) {
    if (fs.existsBlocking(path)) {
      fs.deleteBlocking(path);
    }
    context.assertFalse(fs.existsBlocking(path));
  }

}
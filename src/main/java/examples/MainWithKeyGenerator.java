/*-
 * #%L
 * netrelay
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package examples;

import de.braintags.io.vertx.keygenerator.KeyGeneratorSettings;
import de.braintags.io.vertx.keygenerator.KeyGeneratorVerticle;
import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.init.Settings;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * 
 * @author Michael Remme
 * 
 */

public class MainWithKeyGenerator extends AbstractVerticle {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(MainWithKeyGenerator.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    String settingsPath = "src/main/resources/";
    String settingsFile = settingsPath + "fairytale-settings.json";
    DeploymentOptions options = new DeploymentOptions();
    options.setConfig(new JsonObject().put(Settings.SETTINGS_LOCATION_PROPERTY, settingsFile));

    vertx.deployVerticle(NetRelay.class.getName(), options, result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
        startFuture.fail(result.cause());
      } else {
        LOGGER.info(NetRelay.class.getSimpleName() + " successfully launched: " + result.result());
        initKeyGeneratorVerticle(vertx, settingsPath, startFuture);
      }
    });
  }

  private void initKeyGeneratorVerticle(Vertx vertx, String settingsPath, Future<Void> startFuture) {
    DeploymentOptions options = new DeploymentOptions();
    String settingsLocation = settingsPath + "KeyGeneratorSettings.json";
    LOGGER.info("Settings for KeyGenerator: " + settingsLocation);
    options.setConfig(new JsonObject().put(KeyGeneratorSettings.SETTINGS_LOCATION_PROPERTY, settingsLocation));
    vertx.deployVerticle(KeyGeneratorVerticle.class.getName(), options, result -> {
      if (result.failed()) {
        startFuture.fail(result.cause());
      } else {
        LOGGER.info(KeyGeneratorVerticle.class.getSimpleName() + " successfully launched: " + result.result());
        startFuture.complete();
      }
    });
  }

}

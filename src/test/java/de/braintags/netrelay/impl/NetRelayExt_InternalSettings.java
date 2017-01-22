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

import de.braintags.vertx.util.ErrorObject;
import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.mapper.SimpleNetRelayMapper;
import de.braintags.netrelay.unit.NetRelayBaseTest;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

/**
 * An extension of NetRelay which is getting the {@link Settings} from external
 * 
 * @author Michael Remme
 * 
 */
public class NetRelayExt_InternalSettings extends NetRelay {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(NetRelayExt_InternalSettings.class);

  public static final String SIMPLEMAPPER_NAME = "SimpleNetRelayMapper";
  private Settings settings;
  private static NetRelayExt_InternalSettings netRelay;

  /**
   * 
   */
  protected NetRelayExt_InternalSettings() {
    settings = createDefaultSettings();
    settings.getDatastoreSettings().setDatabaseName("NetRelayExtended_DB");
    settings.getMappingDefinitions().addMapperDefinition(SIMPLEMAPPER_NAME, SimpleNetRelayMapper.class);
  }

  public static NetRelayExt_InternalSettings getInstance(Vertx vertx, TestContext context, NetRelayBaseTest baseTest) {
    if (netRelay == null) {
      netRelay = new NetRelayExt_InternalSettings();
      LOGGER.info("init NetRelay");
      Async async = context.async();
      ErrorObject err = new ErrorObject<>(null);
      baseTest.modifySettings(context, netRelay.settings);
      vertx.deployVerticle(netRelay, result -> {
        if (result.failed()) {
          err.setThrowable(result.cause());
          async.complete();
        } else {
          async.complete();
        }
      });
      async.awaitSuccess();
      if (err.isError()) {
        throw err.getRuntimeException();
      }
    }
    return netRelay;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelay#stop(io.vertx.core.Future)
   */
  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    netRelay = null;
    super.stop(stopFuture);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelay#initSettings()
   */
  @Override
  protected Settings initSettings() {
    NetRelayExt_FileBasedSettings.applySystemProperties(settings);
    return settings;
  }

  /**
   * For test units to be able to modify settings programmatically
   * 
   * @return
   */
  @Override
  public Settings getSettings() {
    return settings;
  }

}

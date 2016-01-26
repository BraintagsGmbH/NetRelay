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

import de.braintags.netrelay.controller.impl.authentication.RegisterController;
import de.braintags.netrelay.routing.RouterDefinition;
import de.braintags.netrelay.util.MultipartUtil;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TRegistration extends NetRelayBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TRegistration.class);

  @Test
  public void testRegister(TestContext context) {
    try {
      resetRoutes();
      MultipartUtil mu = new MultipartUtil();
      mu.addFormField(RegisterController.EMAIL_FIELD_NAME, "user@braintags.de");
      mu.addFormField(RegisterController.PASSWORD_FIELD_NAME, "12345678");
      String url = "/customer/doRegister";

      testRequest(context, HttpMethod.POST, url, req -> {
        mu.finish(req);
      } , resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        LOGGER.info("HEADERS: " + resp.headers);
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  private void resetRoutes() throws Exception {
    RouterDefinition def = netRelay.getSettings().getRouterDefinitions()
        .getNamedDefinition(RegisterController.class.getSimpleName());
    def.setRoutes(new String[] { "/customer/doRegister" });
    def.getHandlerProperties().put("collectionName", "Member");
    netRelay.resetRoutes();
  }

}

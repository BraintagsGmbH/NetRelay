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

import de.braintags.netrelay.controller.impl.BodyController;
import de.braintags.netrelay.controller.impl.MailController;
import de.braintags.netrelay.init.MailClientSettings;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TMailController extends NetRelayBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TMailController.class);

  @Test
  public void sendSimpleMail(TestContext context) {
    try {
      String url = "/api/sendMail";
      Buffer responseBuffer = Buffer.buffer();
      testRequest(context, HttpMethod.POST, url, req -> {
        Buffer buffer = Buffer.buffer();
        buffer.appendString("to=michael.remme%40braintags.de");
        buffer.appendString("&subject=subjectToSend");
        buffer.appendString("&mailText=textToBeSent");

        req.headers().set("content-length", String.valueOf(buffer.length()));
        req.headers().set("content-type", "application/x-www-form-urlencoded");
        req.write(buffer);
      } , resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        context.assertTrue(resp.content.toString().contains("myFirstName"), "Expected name not found");
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  @Override
  protected void modifySettings(TestContext context, Settings settings) {
    super.modifySettings(context, settings);
    MailClientSettings ms = settings.getMailClientSettings();
    ms.setActive(true);
    // defineRouterDefinitions adds the default key-definitions
    RouterDefinition def = defineRouterDefinition(MailController.class, "/api/sendMail");
    def.getHandlerProperties().put(MailController.FROM_PARAM, "unknown@braintags.de");

    settings.getRouterDefinitions().addAfter(BodyController.class.getSimpleName(), def);
  }

}

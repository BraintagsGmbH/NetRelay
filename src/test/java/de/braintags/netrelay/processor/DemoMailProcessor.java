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
package de.braintags.netrelay.processor;

import java.net.URI;

import de.braintags.netrelay.NetRelayBaseTest;
import de.braintags.netrelay.controller.impl.api.MailController;
import de.braintags.netrelay.processor.impl.AbstractMailProcessor;
import de.braintags.netrelay.util.MockRoutingContext;
import io.vertx.core.Future;
import io.vertx.ext.unit.Async;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class DemoMailProcessor extends AbstractMailProcessor {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(DemoMailProcessor.class);

  public static final String DEMO_PROPERTY_KEY = "demoKey";
  public static Async async;

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.processor.impl.AbstractProcessor#handleEvent(io.vertx.core.Future)
   */
  @Override
  protected void handleEvent(Future<Void> future) {
    try {
      LOGGER.info("starting to send message");
      MockRoutingContext context = new MockRoutingContext(vertx, new URI("http://localhost:8080/"));
      context.put(MailController.TO_PARAMETER, NetRelayBaseTest.TESTS_MAIL_RECIPIENT);
      context.put(MailController.SUBJECT_PARAMETER, "Mail gesendet von Processor");
      context.put("TestProperty", "echt ein Testvalue");
      sendMail(context, result -> {
        if (result.failed()) {
          future.fail(result.cause());
        } else {
          if (!result.result().success) {
            future.fail(result.result().errorMessage);
          } else {
            TMailProcessor.eventProcessed = true;
            future.succeeded();
          }
        }
        async.complete();
      });
    } catch (Exception e) {
      future.fail(e);
      async.complete();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.processor.impl.AbstractMailProcessor#internalInit(de.braintags.netrelay.processor.
   * ProcessorDefinition)
   */
  @Override
  protected void internalInit(ProcessorDefinition def) {
    super.internalInit(def);
    TMailProcessor.demoProperty = def.getProcessorProperties().getProperty(DEMO_PROPERTY_KEY);
  }

}

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
package de.braintags.netrelay.processor.impl;

import de.braintags.netrelay.controller.impl.api.MailController;
import de.braintags.netrelay.controller.impl.api.MailController.MailPreferences;
import de.braintags.netrelay.controller.impl.api.MailController.MailSendResult;
import de.braintags.netrelay.processor.IProcessor;
import de.braintags.netrelay.processor.ProcessorDefinition;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * An abstract implementation of an {@link IProcessor}, which is used to send emails, where the content is created by a
 * template. The properties must contain the same than in the {@link MailController}
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractMailProcessor extends AbstractProcessor {
  private MailPreferences mailPrefs;

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.processor.impl.AbstractProcessor#internalInit(de.braintags.netrelay.processor.
   * ProcessorDefinition)
   */
  @Override
  protected void internalInit(ProcessorDefinition def) {
    mailPrefs = MailController.createMailPreferences(vertx, def.getProcessorProperties());
  }

  /**
   * Send a mail by using the template, which is defined inside the {@link MailPreferences} as content template.
   * 
   * @param context
   *          the routing context, which contains all data needed by the defined template
   * @param handler
   *          the handler to be informed
   */
  protected void sendMail(RoutingContext context, Handler<AsyncResult<MailSendResult>> handler) {
    MailController.sendMail(context, netRelay.getMailClient(), mailPrefs, handler);
  }

}

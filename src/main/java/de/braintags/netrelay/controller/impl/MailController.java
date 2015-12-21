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
package de.braintags.netrelay.controller.impl;

import java.util.Properties;

import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.RequestUtil;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.web.RoutingContext;

/**
 * A controller which is sending mails by using the {@link NetRelay#getMailClient()}
 * 
 * @author Michael Remme
 * 
 */
public class MailController extends AbstractController {
  public static final String FROM_PARAM = "from";
  public static final String BOUNCE_ADDRESS_PARAM = "bounceAddress";

  public static final String TO_PARAMETER = "to";
  public static final String SUBJECT_PARAMETER = "subject";
  public static final String TEXT_PARAMETER = "mailText";

  private String from = null;
  private String bounceAddress = null;

  /**
   * 
   */
  public MailController() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext context) {
    MailMessage email = new MailMessage().setFrom(from);
    if (bounceAddress != null)
      email.setBounceAddress("bounce@example.com");
    email.setTo(RequestUtil.readFormAttribute(context, TO_PARAMETER, null, true));
    email.setSubject(RequestUtil.readFormAttribute(context, SUBJECT_PARAMETER, null, false));
    email.setText(RequestUtil.readFormAttribute(context, TEXT_PARAMETER, null, false));

    context.next();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractController#initProperties(java.util.Properties)
   */
  @Override
  public void initProperties(Properties properties) {
    from = readProperty(FROM_PARAM, null, true);
    bounceAddress = readProperty(BOUNCE_ADDRESS_PARAM, null, false);
  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName(MailController.class.getSimpleName());
    def.setBlocking(false);
    def.setController(MailController.class);
    def.setHandlerProperties(getDefaultProperties());
    def.setRoutes(new String[] { "/api/sendmail" });
    return def;
  }

  /**
   * Get the default properties for an implementation of StaticController
   * 
   * @return
   */
  public static Properties getDefaultProperties() {
    Properties json = new Properties();
    json.put(FROM_PARAM, "address@sender.com");
    return json;
  }
}

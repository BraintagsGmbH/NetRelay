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
import io.vertx.ext.mail.MailResult;
import io.vertx.ext.web.RoutingContext;

/**
 * A controller which is sending mails by using the {@link NetRelay#getMailClient()}
 * If the paramter {@value #WAIT_FOR_PARAM} is set to true, then the Controller will wait for the execution and will
 * store the result as {@link MailResult} into the context witht the key {@value #MAIL_RESULT_PARAM}
 * 
 * @author Michael Remme
 * 
 */
public class MailController extends AbstractController {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(MailController.class);

  /**
   * The parameter inside the configuration properties by which the sender of mails is defined
   */
  public static final String FROM_PARAM = "from";

  /**
   * The parameter inside the configuration properties by which the ( optional ) address for bounder is set
   */
  public static final String BOUNCE_ADDRESS_PARAM = "bounceAddress";

  /**
   * The parmameter inside the configuration properties which specifies, wether the controller shall wait for the
   * execution of
   * mail sending and set the status into the context, or wether it shall work asynchron
   */
  public static final String WAIT_FOR_PARAM = "waitForReply";

  /**
   * The name of the parameter inside the request, by which the address to send the mail to is set
   */
  public static final String TO_PARAMETER = "to";

  /**
   * the parameter inside the request, which is specifying the mail subject
   */
  public static final String SUBJECT_PARAMETER = "subject";
  /**
   * The parameter inside the request, which contains the mail text to be sent
   */
  public static final String TEXT_PARAMETER = "mailText";

  /**
   * The parameter inside the request, which contains the HTML text to be sent
   */
  public static final String HTML_PARAMETER = "htmlText";

  /**
   * The key, by which the {@link MailResult} of a sent mail will be stored into the context ( only if waitFor is set to
   * true)
   */
  public static final String MAIL_RESULT_PARAM = "mailResult";

  private String from = null;
  private String bounceAddress = null;
  private boolean waitFor = true;

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
    email.setText(RequestUtil.readFormAttribute(context, TEXT_PARAMETER, "", false));
    String htmlText = RequestUtil.readFormAttribute(context, HTML_PARAMETER, null, false);
    if (htmlText != null) {
      email.setHtml(htmlText);
    }
    if (getNetRelay().getMailClient() == null) {
      context.fail(new IllegalArgumentException(
          "The MailClient of NetRelay is not started, check the configuration and restart server!"));
    } else {
      if (waitFor) {
        getNetRelay().getMailClient().sendMail(email, result -> {
          if (result.failed()) {
            context.fail(result.cause());
          } else {
            context.put(MAIL_RESULT_PARAM, result.result());
            context.next();
          }
        });
      } else {
        getNetRelay().getMailClient().sendMail(email, result -> {
          if (result.failed()) {
            LOGGER.error("error sending mail", result.cause());
          } else {
            LOGGER.info(result.result().toJson());
          }
        });
        context.next();
      }
    }
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
    waitFor = Boolean.parseBoolean(readProperty(WAIT_FOR_PARAM, "true", false));
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
    json.put(WAIT_FOR_PARAM, "true");
    return json;
  }
}

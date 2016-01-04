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
package de.braintags.netrelay.controller.impl.api;

import java.util.Properties;

import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.controller.impl.AbstractController;
import de.braintags.netrelay.controller.impl.ThymeleafTemplateController;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.MailResult;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;

/**
 * A controller which is sending mails by using the {@link NetRelay#getMailClient()}. As a reply a JsonObject with the
 * result will be sent.
 * 
 * <pre>
 * config:

, {
      "name" : "MailController",
      "active" : true,
      "routes" : [ "/api/sendmail" ],
      "blocking" : false,
      "failureDefinition" : false,
      "controller" : "de.braintags.netrelay.controller.impl.api.MailController",
      "httpMethod" : null,
      "handlerProperties" : {
        "templateDirectory" : "templates",
        "mode" : "XHTML",
        "cacheEnabled" : "true",
        "from" : "address@sender.com"
      },
      "captureCollection" : null
    }

Parameter ( lassen sich entweder per config oder per request parameter setzen:

to - an wen geht die Mail
from: absender
subject: titel der Mail
mailText: text einer Mail für textbasierten Inhalt
htmlText: HTML Inhalt einer Mail
template: der Pfad eines Templates im Template-Verzeichnis. Wird geparsed mit Thymeleaf und dann als Inhalt geschickt. Überschreibt htmlText
 * 
 * 
 * </pre>
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
   * With this parameter the template can be set, which will be parsed to generate the content of the mail
   */
  public static final String TEMPLATE_PARAM = "template";

  private String from = null;
  private String bounceAddress = null;
  private ThymeleafTemplateEngine templateEngine;
  private String templateDirectory;

  private static final String UNCONFIGURED_ERROR = "The MailClient of NetRelay is not started, check the configuration and restart server!";

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext context) {
    MailSendResult sendResult = new MailSendResult();
    createMailMessage(context, result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
        sendResult.errorMessage = result.cause().toString();
        sendReply(context, sendResult);
      } else {
        MailMessage email = result.result();
        sendMessage(context, email, sendResult);
      }
    });
  }

  /**
   * @param context
   * @param email
   * @param sendResult
   */
  private void sendMessage(RoutingContext context, MailMessage email, MailSendResult sendResult) {
    if (getNetRelay().getMailClient() == null) {
      LOGGER.error(new IllegalArgumentException(UNCONFIGURED_ERROR));
      sendResult.errorMessage = UNCONFIGURED_ERROR;
      sendReply(context, sendResult);
    } else {
      getNetRelay().getMailClient().sendMail(email, result -> {
        if (result.failed()) {
          LOGGER.error("", result.cause());
          sendResult.errorMessage = result.cause().toString();
        } else {
          sendResult.success = true;
          sendResult.mailResult = result.result();
        }
        sendReply(context, sendResult);
      });
    }
  }

  private void sendReply(RoutingContext context, MailSendResult result) {
    HttpServerResponse response = context.response();
    response.putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(result));
  }

  /**
   * @param context
   * @return
   */
  private void createMailMessage(RoutingContext context, Handler<AsyncResult<MailMessage>> handler) {
    String mailFrom = readParameterOrProperty(context, FROM_PARAM, null, true);
    MailMessage email = new MailMessage().setFrom(mailFrom);
    if (bounceAddress != null) {
      email.setBounceAddress(bounceAddress);
    }
    email.setTo(readParameterOrProperty(context, TO_PARAMETER, null, true));
    email.setSubject(readParameterOrProperty(context, SUBJECT_PARAMETER, "undefined", false));
    String template = readParameterOrProperty(context, TEMPLATE_PARAM, null, false);
    email.setHtml(readParameterOrProperty(context, HTML_PARAMETER, "", false));
    email.setText(readParameterOrProperty(context, TEXT_PARAMETER, "", false));
    if (template != null && template.hashCode() != 0) {
      String file = templateDirectory + "/" + template;
      templateEngine.render(context, file, res -> {
        if (res.succeeded()) {
          email.setHtml(res.result().toString());
          handler.handle(Future.succeededFuture(email));
        } else {
          handler.handle(Future.failedFuture(res.cause()));
        }
      });
    } else {
      handler.handle(Future.succeededFuture(email));
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractController#initProperties(java.util.Properties)
   */
  @Override
  public void initProperties(Properties properties) {
    from = readProperty(FROM_PARAM, null, false);
    bounceAddress = readProperty(BOUNCE_ADDRESS_PARAM, null, false);
    templateEngine = ThymeleafTemplateController.createTemplateEngine(properties);
    templateDirectory = ThymeleafTemplateController.getTemplateDirectory(properties);
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
    json.put(ThymeleafTemplateController.TEMPLATE_MODE_PROPERTY, ThymeleafTemplateEngine.DEFAULT_TEMPLATE_MODE);
    json.put(ThymeleafTemplateController.CACHE_ENABLED_PROPERTY, "true");
    json.put(ThymeleafTemplateController.TEMPLATE_DIRECTORY_PROPERTY,
        ThymeleafTemplateController.DEFAULT_TEMPLATE_DIRECTORY);
    return json;
  }

  class MailSendResult {
    public boolean success = false;
    public String errorMessage;
    public MailResult mailResult;
  }
}

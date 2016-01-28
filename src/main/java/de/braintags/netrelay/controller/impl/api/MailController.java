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

import de.braintags.io.vertx.util.exception.InitException;
import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.controller.impl.AbstractController;
import de.braintags.netrelay.controller.impl.ThymeleafTemplateController;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.mail.MailClient;
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

to - an wen geht die Mail, gelesen aus Config, Request-Parametern oder Context
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
  // TODO change composer and sender to Verticle - https://github.com/BraintagsGmbH/netrelay/issues/3

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

  private static final String UNCONFIGURED_ERROR = "The MailClient of NetRelay is not started, check the configuration and restart server!";

  private MailPreferences prefs;

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext context) {
    sendMail(context, getNetRelay().getMailClient(), prefs, result -> {
      sendReply(context, result.result());
    });
  }

  private void sendReply(RoutingContext context, MailSendResult result) {
    HttpServerResponse response = context.response();
    response.putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(result));
  }

  /**
   * The method composes and sends a mail message. Note: this method won't cause a fail on the given handler, it calls
   * always the success method of the handler. If errors occured, they will be set inside the returned
   * {@link MailSendResult},
   * where then the property {@link MailSendResult#success} is set to false
   * 
   * @param context
   *          the current context
   * @param mailClient
   *          the {@link MailClient} to be used
   * @param prefs
   *          the {@link MailPreferences} created from the controller properties
   * @param handler
   *          the handler to be informed. The handler receives an instance of {@link MailSendResult}
   */
  public static void sendMail(RoutingContext context, MailClient mailClient, MailPreferences prefs,
      Handler<AsyncResult<MailSendResult>> handler) {
    try {
      createMailMessage(context, prefs, result -> {
        if (result.failed()) {
          LOGGER.error("", result.cause());
          MailSendResult msResult = new MailSendResult(result.cause());
          handler.handle(Future.succeededFuture(msResult));
        } else {
          MailMessage email = result.result();
          sendMessage(context, mailClient, email, handler);
        }
      });
    } catch (Exception e) {
      LOGGER.error("", e);
      MailSendResult msResult = new MailSendResult(e);
      handler.handle(Future.succeededFuture(msResult));
    }
  }

  /**
   * @param context
   * @param email
   * @param sendResult
   */
  private static void sendMessage(RoutingContext context, MailClient mailClient, MailMessage email,
      Handler<AsyncResult<MailSendResult>> handler) {
    mailClient.sendMail(email, result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
        MailSendResult msResult = new MailSendResult(result);
        handler.handle(Future.succeededFuture(msResult));
      } else {
        MailSendResult msResult = new MailSendResult(result);
        handler.handle(Future.succeededFuture(msResult));
      }
    });
  }

  /**
   * @param context
   * @return
   */
  private static void createMailMessage(RoutingContext context, MailPreferences prefs,
      Handler<AsyncResult<MailMessage>> handler) {
    String mailFrom = prefs.from == null ? readParameter(context, FROM_PARAM, true) : prefs.from;
    MailMessage email = new MailMessage().setFrom(mailFrom);
    if (prefs.bounceAddress != null) {
      email.setBounceAddress(prefs.bounceAddress);
    }
    String to = prefs.to == null ? readParameterOrContext(context, TO_PARAMETER, null, true) : prefs.to;
    email.setTo(to);
    String subject = prefs.subject == null ? readParameterOrContext(context, SUBJECT_PARAMETER, null, true)
        : prefs.subject;
    email.setSubject(subject);
    String html = prefs.html == null ? readParameterOrContext(context, HTML_PARAMETER, null, true) : prefs.html;
    email.setHtml(html);
    String text = prefs.text == null ? readParameterOrContext(context, TEXT_PARAMETER, null, true) : prefs.text;
    email.setText(text);
    String template = prefs.template == null ? readParameterOrContext(context, TEMPLATE_PARAM, null, false)
        : prefs.template;
    if (template != null && template.hashCode() != 0) {
      String file = prefs.templateDirectory + "/" + template;
      prefs.templateEngine.render(context, file, res -> {
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

  /**
   * Create a new instance of {@link MailPreferences} with the given properties
   * 
   * @param properties
   *          the properties to be used
   * @return a new instance
   */
  public static MailPreferences createMailPreferences(Properties properties) {
    return new MailPreferences(properties);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractController#initProperties(java.util.Properties)
   */
  @Override
  public void initProperties(Properties properties) {
    if (getNetRelay().getMailClient() == null) {
      throw new InitException(UNCONFIGURED_ERROR);
    }
    prefs = createMailPreferences(properties);
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

  /**
   * Preferences, which are defining the behaviour of the MailController
   * 
   * @author Michael Remme
   *
   */
  public static class MailPreferences {
    private String to;
    private String from = null;
    private String subject = null;
    private String bounceAddress = null;
    private ThymeleafTemplateEngine templateEngine;
    private String templateDirectory;
    private String template;
    private String html;
    private String text;

    /**
     * 
     */
    MailPreferences(Properties props) {
      from = readProperty(props, FROM_PARAM, null, false);
      bounceAddress = readProperty(props, BOUNCE_ADDRESS_PARAM, null, false);
      to = readProperty(props, TO_PARAMETER, null, false);
      subject = readProperty(props, SUBJECT_PARAMETER, null, false);
      template = readProperty(props, TEMPLATE_PARAM, null, false);
      templateEngine = ThymeleafTemplateController.createTemplateEngine(props);
      templateDirectory = ThymeleafTemplateController.getTemplateDirectory(props);
      html = readProperty(props, HTML_PARAMETER, "", false);
      text = readProperty(props, TEXT_PARAMETER, "", false);
    }

  }

  /**
   * The result of a mail composing and sending
   * 
   * @author Michael Remme
   *
   */
  public static class MailSendResult {
    public boolean success = false;
    public String errorMessage;
    public MailResult mailResult;

    MailSendResult(Throwable exception) {
      success = false;
      errorMessage = exception.toString();
    }

    MailSendResult(AsyncResult<MailResult> result) {
      if (result.failed()) {
        success = false;
        errorMessage = result.cause().toString();
      } else {
        success = true;
        mailResult = result.result();
      }
    }
  }
}

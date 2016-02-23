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
package de.braintags.netrelay.controller.impl.authentication;

import java.util.List;
import java.util.Properties;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.util.QueryHelper;
import de.braintags.io.vertx.util.exception.InitException;
import de.braintags.netrelay.RequestUtil;
import de.braintags.netrelay.controller.impl.AbstractController;
import de.braintags.netrelay.controller.impl.api.MailController;
import de.braintags.netrelay.controller.impl.api.MailController.MailSendResult;
import de.braintags.netrelay.controller.impl.persistence.PersistenceController;
import de.braintags.netrelay.model.IAuthenticatable;
import de.braintags.netrelay.model.Member;
import de.braintags.netrelay.model.PasswordLostClaim;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * The PasswordLostController is used to manage the process for a user, if he lost his password.<br/>
 * The entry is an http-form, which you will create and which contains a field to enter an email address. The name of
 * this field must be the same, like you defined in the controller properties with the property
 * {@value #EMAIL_FIELD_NAME_PROP}, which by default is "email". The destination of the form must be a route, which is
 * covered by the current controller ( for instance /customer/passwordLost ). <br/>
 * 
 * The controller knows two actions:<br/>
 * - the start of the password lost process<br/>
 * where a user filled out a the email address and sends the form. The start of the
 * password lost is activated, when the request contains a form parameter {@value #EMAIL_FIELD_NAME_PROP}<br/>
 * - the confirmation of a password lost<br/>
 * which is typically performed when a user clicked a link in a confirmation mail and is activated, when the request
 * contains the parameter {@value #VALIDATION_ID_PARAM}<br/>
 * If none of this fits, then the controller will throw an exception java.lang.IllegalArgumentException: invalid action
 * for password lost process<br/>
 * <br/>
 * <br/>
 * At first, for the reset process, the system chechs the existence of an account for the given email address. If found,
 * the controller creates a new instance of {@link PasswordLostClaim}, which contains all needed data to finish the
 * process in step 2. Previously created instances of PasswordLostClaim with the same email address are deactivated.
 * <br/>
 * The id of the PasswordLostClaim is stored in the context under the property {@value #VALIDATION_ID_PARAM},
 * additionally the PasswordLostClaim itself is stored inside the context under "PasswordLostClaim". Further the email
 * address of the found Member etc. is stored in the context under the parameter {@link MailController#TO_PARAMETER}, so
 * that the MailController can use it later on to send the message. Additionally all request parameters are added to the
 * context, so that they can be used as content for the generated mail.<br/>
 * After this, the MailController is called to compose and send the conformation mail to the client. The configuration
 * of the MailController must be contained inside the configuration of this Controller. The template, which is
 * part of that configuration, will be used to compose the confirmation mail, where the confirmation link must be
 * contained. The confirmation link has the structure:
 * <p>
 * confirmationPage?{@value #VALIDATION_ID_PARAM}=ID<br/>
 * The confirmation page can be any virtual page and must be defined as route for the PasswordLostController, so that it
 * is reacting to it. The ID is the ID, which was stored before in the context.<br/>
 * </p>
 * 
 * After successfully processing the MailController, the success page, defined by {@value #REG_START_SUCCESS_URL_PROP},
 * is called. If anything failed, then the information about the error are stored under the parameter
 * {@value #RESET_ERROR_PARAM} inside the context and the error page defined by
 * {@value #PW_LOST_FAIL_URL_PROP} is called.<br/>
 * 
 * When a user clicks the link in the mail, the PasswordLostController will perform the confirmation. It will fetch the
 * instance of PasswordLostClaim, which was previously created, will fetch the IAuthenticatable from the datastore and
 * will store it under the property {@link #AUTHENTICATABLE_PROP} . After that it will call the success page, defined by
 * {@value #PW_RESET_SUCCESS_URL_PROP}. If anything failed, then the information about the error are stored under the
 * parameter {@value #RESET_ERROR_PARAM} inside the context and the error page defined by
 * {@value #PW_RESET_FAIL_URL_PROP} is called. The success page will typically display a form, where the new password
 * and a confirmation password can be entered and saved. The form then will refer to a page, where the
 * {@link PersistenceController} is executed.
 * 
 * <br/>
 * <br/>
 * Config-Parameter:<br/>
 * possible parameters, which are read from the configuration
 * <UL>
 * <LI>{@value #PW_LOST_SUCCESS_URL_PROP}
 * <LI>{@value #PW_LOST_FAIL_URL_PROP}
 * <LI>{@value #PW_RESET_SUCCESS_URL_PROP}
 * <LI>{@value #PW_RESET_FAIL_URL_PROP}
 * 
 * <LI>{@value #AUTHENTICATABLE_CLASS_PROP }
 * <LI>{@value #EMAIL_FIELD_NAME_PROP }
 * 
 * <LI>{@value #EMAIL_FIELD_NAME_PROP} - defines the name of the field, which transports the email address to be used
 * for the reset process
 * 
 * <LI>Additionally the config-parameters of {@link MailController} must be set
 * </UL>
 * <br>
 * 
 * Request-Parameter:<br/>
 * possible parameters, which are read from a request
 * <UL>
 * <LI>email - defined by {@value #EMAIL_FIELD_NAME_PROP } and used to transport the email address of the account to
 * reset the password for
 * <LI>{@value #VALIDATION_ID_PARAM} - used for the confirmation to transport the id of the PasswordLostClaim created in
 * the first step
 * </UL>
 * <br/>
 * 
 * Result-Parameter:<br/>
 * possible paramters, which will be placed into the context
 * <UL>
 * <LI>{@value #AUTHENTICATABLE_PROP} - in step 1 and 2, if successfull, the Member etc. is stored under this property
 * <LI>{@value #RESET_ERROR_PARAM} if an error occured in Step 1 or 2, the code of the error is stored under this
 * property. Possible codes are defined by {@link PasswordLostCode}
 * <LI>{@value #MAIL_SEND_RESULT_PROP} STEPT 1 - if sending of the confirmation mail failed, it is stored under this
 * property
 * <LI>{@value #VALIDATION_ID_PARAM} - STEP 1 - on a successfull create action, the ID of {@link PasswordLostClaim} is
 * stored
 * here and the request is redirected to the success page, where the confirmation mail is created and sent. This
 * parameter is keeping the confirmation id, which must be integrated into the link
 * <LI>PasswordLostClaim - Step 1 - on a successfull create action, the {@link PasswordLostClaim} is stored here
 * 
 * </UL>
 * <br/>
 * 
 * 
 * @author Michael Remme
 * 
 */
public class PasswordLostController extends AbstractController {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(PasswordLostController.class);

  /**
   * The url, which shall be called after the password lost process has been successfully started. Means: the email
   * adress could be found an the email with the reset link was successfully sent
   */
  public static final String PW_LOST_SUCCESS_URL_PROP = "pwLostSuccessUrl";

  /**
   * The url, which shall be called, when the password lost process should be started, but failed
   */
  public static final String PW_LOST_FAIL_URL_PROP = "pwLostFailUrl";

  /**
   * The url, which shall be called, after the user clicked the link inside the password lost message, which was sent
   * before, and after the {@link PasswordLostClaim} was successfully verified.
   */
  public static final String PW_RESET_SUCCESS_URL_PROP = "pwResetSuccessUrl";

  /**
   * The url, which shall be called, if the {@link PasswordLostClaim} could not be successfully verified
   */
  public static final String PW_RESET_FAIL_URL_PROP = "pwResetFailUrl";

  /**
   * The name of the class - as instance of {@link IAuthenticatable} - which will be used to save a successful and
   * improved registration as {@link Member} for instance
   */
  public static final String AUTHENTICATABLE_CLASS_PROP = "authenticatableClass";

  /**
   * This property defines the name of the parameter, by which the email address is transported, when the password lost
   * process is started
   */
  public static final String EMAIL_FIELD_NAME_PROP = "emailFieldName";

  /**
   * The name of the parameter which is used to store error information in the context
   */
  public static final String RESET_ERROR_PARAM = "resetError";

  /**
   * The name of the parameter, which keeps the validation ID. This is the name of the parametername of the link, which
   * will be sent by the confirmation mail
   */
  public static final String VALIDATION_ID_PARAM = "validationId";

  /**
   * When the confirmation link was clicked, then the system fetches the instance of {@link IAuthenticatable}, like a
   * {@link Member}, and stores it under this property into the context
   */
  public static final String AUTHENTICATABLE_PROP = "authenticatable";

  /**
   * The name of the property which is used to store the {@link MailSendResult} in the context, if the mail sending
   * failed
   */
  private static final String MAIL_SEND_RESULT_PROP = "mailSendResult";

  private String successUrl;
  private String failUrl;
  private String successConfirmUrl;
  private String failConfirmUrl;
  private Class<? extends IAuthenticatable> authenticatableCLass;
  private MailController.MailPreferences mailPrefs;

  private String emailFieldName;

  /**
   * 
   */
  public PasswordLostController() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext context) {
    if (hasParameter(context, emailFieldName)) {
      passwordLostStart(context);
    } else if (hasParameter(context, VALIDATION_ID_PARAM)) {
      passwordLostConfirm(context);
    } else {
      context.put(RESET_ERROR_PARAM, PasswordLostCode.INVALID_ACTION.toString());
      context.reroute(failUrl);
    }
  }

  private void passwordLostStart(RoutingContext context) {
    try {
      String email = readParameter(context, emailFieldName, true);
      getUser(email, userRes -> {
        if (userRes.failed()) {
          context.put(RESET_ERROR_PARAM, userRes.cause().getMessage());
          context.reroute(failUrl);
        } else {
          IAuthenticatable member = userRes.result();
          createPasswordLostClaim(context, email, rcRes -> {
            if (rcRes.failed()) {
              String message = rcRes.cause().getMessage();
              context.put(RESET_ERROR_PARAM, message);
              context.reroute(failUrl);
            } else {
              PasswordLostClaim rc = rcRes.result();
              LOGGER.info("Created PasswordLostClaim id is " + rc.id);
              context.put(AUTHENTICATABLE_PROP, member);
              addParameterToContext(context, rc);
              MailController.sendMail(context, getNetRelay().getMailClient(), mailPrefs, result -> {
                MailController.MailSendResult msResult = result.result();
                if (msResult.success) {
                  RequestUtil.sendRedirect(context.response(), successUrl);
                } else {
                  context.put(MAIL_SEND_RESULT_PROP, msResult);
                  context.reroute(failUrl);
                }
              });
            }
          });
        }
      });
    } catch (Exception e) {
      LOGGER.error("", e);
      context.put(RESET_ERROR_PARAM, e.getMessage());
      context.reroute(failUrl);
    }
  }

  private void addParameterToContext(RoutingContext context, PasswordLostClaim claim) {
    claim.requestParameter.entrySet().forEach(entry -> context.put(entry.getKey(), entry.getValue()));
  }

  private void deactivatePreviousClaims(RoutingContext context, String email, Handler<AsyncResult<Void>> handler) {
    IQuery<PasswordLostClaim> query = getNetRelay().getDatastore().createQuery(PasswordLostClaim.class);
    query.field("email").is(email).field("active").is(true);
    QueryHelper.executeToList(query, qr -> {
      if (qr.failed()) {
        handler.handle(Future.failedFuture(qr.cause()));
      } else {
        List<PasswordLostClaim> cl = (List<PasswordLostClaim>) qr.result();
        if (!cl.isEmpty()) {
          IWrite<PasswordLostClaim> write = getNetRelay().getDatastore().createWrite(PasswordLostClaim.class);
          cl.forEach(rc -> rc.active = false);
          write.addAll(cl);
          write.save(wr -> {
            if (wr.failed()) {
              handler.handle(Future.failedFuture(wr.cause()));
            } else {
              handler.handle(Future.succeededFuture());
            }
          });
        } else {
          handler.handle(Future.succeededFuture());
        }
      }
    });
  }

  private void createPasswordLostClaim(RoutingContext context, String email,
      Handler<AsyncResult<PasswordLostClaim>> handler) {
    deactivatePreviousClaims(context, email, previous -> {
      if (previous.failed()) {
        handler.handle(Future.failedFuture(previous.cause()));
      } else {
        PasswordLostClaim rc = new PasswordLostClaim(email, context.request());
        IWrite<PasswordLostClaim> write = getNetRelay().getDatastore().createWrite(PasswordLostClaim.class);
        write.add(rc);
        write.save(sr -> {
          if (sr.failed()) {
            LOGGER.error("", sr.cause());
            handler.handle(Future.failedFuture(sr.cause()));
          } else {
            context.put(PasswordLostClaim.class.getSimpleName(), rc);
            context.put(MailController.TO_PARAMETER, email);
            context.put(VALIDATION_ID_PARAM, rc.id);
            handler.handle(Future.succeededFuture(rc));
          }
        });
      }
    });
  }

  private void getUser(String email, Handler<AsyncResult<IAuthenticatable>> handler) {
    if (email == null || email.hashCode() == 0) {
      handler.handle(Future.failedFuture(PasswordLostCode.EMAIL_REQUIRED.toString()));
    } else {
      IQuery<? extends IAuthenticatable> query = getNetRelay().getDatastore().createQuery(this.authenticatableCLass);
      query.field("email").is(email);
      QueryHelper.executeToList(query, qr -> {
        if (qr.failed()) {
          LOGGER.error("", qr.cause());
          handler.handle(Future.failedFuture(qr.cause()));
        } else {
          if (qr.result().size() == 0) {
            handler.handle(Future.failedFuture(PasswordLostCode.NO_ACCOUNT.toString()));
          } else {
            handler.handle(Future.succeededFuture((IAuthenticatable) qr.result().get(0)));
          }
        }
      });
    }
  }

  private void passwordLostConfirm(RoutingContext context) {
    try {
      String claimId = context.request().getParam(VALIDATION_ID_PARAM);
      QueryHelper.findRecordById(getNetRelay().getDatastore(), PasswordLostClaim.class, claimId, cr -> {
        if (cr.failed()) {
          context.fail(cr.cause());
        } else {
          if (cr.result() == null) {
            context.put(RESET_ERROR_PARAM, PasswordLostCode.CONFIRMATION_FAILURE);
            context.reroute(failConfirmUrl);
          } else {
            PasswordLostClaim rc = (PasswordLostClaim) cr.result();
            getUser(rc.email, acRes -> {
              if (acRes.failed()) {
                LOGGER.error("", acRes.cause());
                context.put(RESET_ERROR_PARAM, acRes.cause().getMessage());
                context.reroute(failConfirmUrl);
              } else {
                deactivateClaim(rc);
                context.put(AUTHENTICATABLE_PROP, acRes.result());
                context.reroute(successConfirmUrl);
              }
            });
          }
        }
      });
    } catch (Exception e) {
      LOGGER.error("", e);
      context.put(RESET_ERROR_PARAM, e.getMessage());
      context.reroute(failConfirmUrl);
    }
  }

  /**
   * We don't wait for it
   * 
   * @param claim
   * @param handler
   */
  private void deactivateClaim(PasswordLostClaim claim) {
    claim.active = false;
    IWrite<PasswordLostClaim> write = getNetRelay().getDatastore().createWrite(PasswordLostClaim.class);
    write.add(claim);
    write.save(wr -> {
      if (wr.failed()) {
        LOGGER.warn("", wr.cause());
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractController#initProperties(java.util.Properties)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void initProperties(Properties properties) {
    successUrl = readProperty(PW_LOST_SUCCESS_URL_PROP, null, true);
    failUrl = readProperty(PW_LOST_FAIL_URL_PROP, null, true);
    successConfirmUrl = readProperty(PW_RESET_SUCCESS_URL_PROP, null, true);
    failConfirmUrl = readProperty(PW_RESET_FAIL_URL_PROP, null, true);
    try {
      authenticatableCLass = (Class<? extends IAuthenticatable>) Class
          .forName(readProperty(AUTHENTICATABLE_CLASS_PROP, Member.class.getName(), false));
    } catch (ClassNotFoundException e) {
      throw new InitException(e);
    }
    mailPrefs = MailController.createMailPreferences(getVertx(), properties);
    emailFieldName = readProperty(EMAIL_FIELD_NAME_PROP, "email", false);
  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName(PasswordLostController.class.getSimpleName());
    def.setBlocking(false);
    def.setController(PasswordLostController.class);
    def.setHandlerProperties(getDefaultProperties());
    def.setRoutes(new String[] { "/customer/passwordLost", "/customer/passwordReset" });
    return def;
  }

  /**
   * Get the default properties for an implementation of StaticController
   * 
   * @return
   */
  public static Properties getDefaultProperties() {
    Properties json = new Properties();
    json.put(PW_LOST_SUCCESS_URL_PROP, "/customer/passwordLostSuccess.html");
    json.put(PW_LOST_FAIL_URL_PROP, "/customer/passwordLostFail.html");
    json.put(PW_RESET_SUCCESS_URL_PROP, "/customer/passwordResetSuccess.html");
    json.put(PW_RESET_FAIL_URL_PROP, "/customer/passwordResetFail.html");
    json.put(AUTHENTICATABLE_CLASS_PROP, Member.class.getName());
    json.put(EMAIL_FIELD_NAME_PROP, "email");

    return json;
  }

}

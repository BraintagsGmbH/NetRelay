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
package de.braintags.netrelay.controller.impl.authentication;

import java.util.Properties;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.util.exception.InitException;
import de.braintags.netrelay.controller.impl.AbstractController;
import de.braintags.netrelay.model.IAuthenticatable;
import de.braintags.netrelay.model.Member;
import de.braintags.netrelay.model.RegisterClaim;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * The controller reads several properties from the current request, performs a password check, creates and saves a new
 * member and performs a login for the new member.
 * The properties, which must be sent by a request are:
 * 
 * After the action the page defined by the properties registerSuccess or registerFailure is called
 * 
 * Config-Parameter:<br/>
 * <UL>
 * <LI>{@value #SUCCESS_URL_PROP} - defines the url which is used, when the registration claim was successful
 * <LI>{@value #FAIL_URL_PROP} - defines the url which is used, when the registration claim raised an error
 * <LI>{@value #AUTHENTICATABLE_CLASS_PROP} - the property name, which defines the class, which will be used to generate
 * a new member, user, customer etc.
 * </UL>
 * <br>
 * Request-Parameter:<br/>
 * The new instance is created by two fields first:
 * <UL>
 * <LI>email
 * <LI>password
 * </UL>
 * additional fields can be set by fields with the structure mapper.fieldName
 * <br/>
 * Result-Parameter:<br/>
 * <UL>
 * <LI>{@value #REGISTER_ERROR_PARAM} the parameter, where an error String of a failed registration is stored in
 * the context. The codes are defined by {@link RegistrationCode}
 * <LI>RegisterClaim - on a successfull create action, the RegisterClaim is stored here, where the id can be used to
 * generate the confirmation link
 * </UL>
 * <br/>
 * 
 * @author Michael Remme
 *
 */
public class RegisterController extends AbstractController {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(RegisterController.class);

  /**
   * The url, which shall be called after a successful register
   */
  public static final String SUCCESS_URL_PROP = "successUrl";

  /**
   * The url, which shall be called after a failed register
   */
  public static final String FAIL_URL_PROP = "failUrl";

  /**
   * The name of the class - as instance of {@link IAuthenticatable} - which will be used to save a successful and
   * improved registration
   */
  public static final String AUTHENTICATABLE_CLASS_PROP = "authenticatableClass";

  /**
   * Property defines, whether the system checks, wether an email exists already in the datastore
   */
  public static final String ALLOW_DUPLICATION_EMAIL_PROP = "allowDuplicateEmail";

  /**
   * The name of the parameter which is used to store error information in the context
   */
  public static final String REGISTER_ERROR_PARAM = "registerError";

  /**
   * The name of the field used to send the password
   */
  public static final String PASSWORD_FIELD_NAME = "password";

  /**
   * The name of the field used to send the email
   */
  public static final String EMAIL_FIELD_NAME = "email";

  private String successUrl;
  private String failUrl;
  private Class<? extends IAuthenticatable> authenticatableCLass;
  private boolean allowDuplicateEmail;

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext context) {
    if (hasParameter(context, PASSWORD_FIELD_NAME)) {
      registerStart(context);
    } else {
      registerConfirm(context);
    }
  }

  private void registerStart(RoutingContext context) {
    try {
      String password = readParameter(context, PASSWORD_FIELD_NAME, false);
      String email = readParameter(context, EMAIL_FIELD_NAME, false);
      checkEmail(email, emailRes -> {
        if (emailRes.failed()) {
          context.put(REGISTER_ERROR_PARAM, emailRes.cause().getMessage());
          context.reroute(failUrl);
        } else {
          checkPassword(password, pwRes -> {
            if (pwRes.failed()) {
              context.put(REGISTER_ERROR_PARAM, pwRes.cause().getMessage());
              context.reroute(failUrl);
            } else {
              createRegisterClaim(context, rcRes -> {
                if (rcRes.failed()) {
                  context.put(REGISTER_ERROR_PARAM, rcRes.cause().getMessage());
                  context.reroute(failUrl);
                } else {
                  context.reroute(successUrl);
                }
              });
            }
          });
        }
      });
    } catch (Exception e) {
      LOGGER.error("", e);
      context.put(REGISTER_ERROR_PARAM, e.getMessage());
      context.reroute(failUrl);
    }
  }

  private void createRegisterClaim(RoutingContext context, Handler<AsyncResult<Void>> handler) {
    RegisterClaim rc = new RegisterClaim(context.request());
    IWrite<RegisterClaim> write = getNetRelay().getDatastore().createWrite(RegisterClaim.class);
    write.add(rc);
    write.save(sr -> {
      if (sr.failed()) {
        LOGGER.error("", sr.cause());
        handler.handle(Future.failedFuture(sr.cause()));
      } else {
        context.put(RegisterClaim.class.getSimpleName(), rc);
        handler.handle(Future.succeededFuture());
      }
    });
  }

  private void checkPassword(String password, Handler<AsyncResult<RegistrationCode>> handler) {
    if (password == null || password.hashCode() == 0) {
      handler.handle(Future.failedFuture(RegistrationCode.PASSWORD_REQUIRED.toString()));
    } else {
      handler.handle(Future.succeededFuture(RegistrationCode.OK));
    }
  }

  private void checkEmail(String email, Handler<AsyncResult<RegistrationCode>> handler) {
    if (email == null || email.hashCode() == 0) {
      handler.handle(Future.failedFuture(RegistrationCode.EMAIL_REQUIRED.toString()));
    } else if (!allowDuplicateEmail) {
      IQuery<? extends IAuthenticatable> query = getNetRelay().getDatastore().createQuery(this.authenticatableCLass);
      query.field("email").is(email);
      query.executeCount(qr -> {
        if (qr.failed()) {
          LOGGER.error("", qr.cause());
          handler.handle(Future.failedFuture(qr.cause()));
        } else {
          if (qr.result().getCount() > 0) {
            handler.handle(Future.failedFuture(RegistrationCode.EMAIL_EXISTS.toString()));
          } else {
            handler.handle(Future.succeededFuture(RegistrationCode.OK));
          }
        }
      });
    } else {
      handler.handle(Future.succeededFuture(RegistrationCode.OK));
    }
  }

  private void registerConfirm(RoutingContext context) {
    context.fail(new UnsupportedOperationException());
  }

  private void storeMember(RoutingContext context, Member member) {
    IWrite<Member> write = getNetRelay().getDatastore().createWrite(Member.class);
    write.add(member);
    write.save(result -> {
      if (result.failed()) {
        LOGGER.error("error saving member", result.cause());
        String message = result.cause().getLocalizedMessage();
        context.put("generalError", message);
        context.reroute(failUrl);
      } else {
        context.put(Member.CURRENT_USER_PROPERTY, member);
        context.reroute(successUrl);
      }
    });
  }

  // private IAuthenticatable createMember(RoutingContext context, JsonObject errorObject) {
  // IAuthenticatable member = this.authenticatableCLass.newInstance();
  //
  // return member;
  // }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractController#initProperties(java.util.Properties)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void initProperties(Properties properties) {
    successUrl = readProperty(SUCCESS_URL_PROP, null, true);
    failUrl = readProperty(FAIL_URL_PROP, null, true);
    try {
      authenticatableCLass = (Class<? extends IAuthenticatable>) Class
          .forName(readProperty(AUTHENTICATABLE_CLASS_PROP, Member.class.getName(), false));
    } catch (ClassNotFoundException e) {
      throw new InitException(e);
    }
    allowDuplicateEmail = Boolean.valueOf(readProperty(ALLOW_DUPLICATION_EMAIL_PROP, "false", false));

  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName(RegisterController.class.getSimpleName());
    def.setBlocking(false);
    def.setController(RegisterController.class);
    def.setHandlerProperties(getDefaultProperties());
    def.setRoutes(new String[] { "/customer/doRegister" });
    return def;
  }

  /**
   * Get the default properties for an implementation of StaticController
   * 
   * @return
   */
  public static Properties getDefaultProperties() {
    Properties json = new Properties();
    json.put(SUCCESS_URL_PROP, "/customer/registerSuccess.html");
    json.put(FAIL_URL_PROP, "/customer/register.html");
    json.put(AUTHENTICATABLE_CLASS_PROP, Member.class.getName());
    json.put(ALLOW_DUPLICATION_EMAIL_PROP, "false");
    return json;
  }

}

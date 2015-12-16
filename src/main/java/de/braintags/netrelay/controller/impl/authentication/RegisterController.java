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

import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.netrelay.controller.impl.AbstractController;
import de.braintags.netrelay.model.Member;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * The controller reads several properties from the current request, performs a password check, creates and saves a new
 * member and performs a login for the new member.
 * The properties, which must be sent by a request are:
 * - username
 * - email
 * - firstName
 * - lastName
 * - password
 * - retypePassword
 * 
 * After the action the page defined by the properties registerSuccess or registerFailure is called
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

  private String successUrl;
  private String failUrl;

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext context) {
    JsonObject errorObject = new JsonObject();
    String password = loadProperty(context, "password", true, errorObject);
    String retypepassword = loadProperty(context, "retypepassword", true, errorObject);
    if (!password.equals(retypepassword)) {
      context.put("passwordError", "die Passworte sind nicht gleich");
      context.reroute(failUrl);
    } else {
      Member member = createMember(context, errorObject);
      if (!errorObject.isEmpty()) {
        context.put("generalError", "There are errors in fields: " + errorObject.toString());
        context.reroute(failUrl);
      } else {
        storeMember(context, member);
      }
    }
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

  private Member createMember(RoutingContext context, JsonObject errorObject) {
    Member member = new Member();
    member.setUserName(loadProperty(context, "username", true, errorObject));
    member.setFirstName(loadProperty(context, "firstName", false, errorObject));
    member.setEmail(loadProperty(context, "email", true, errorObject));
    member.setLastName(loadProperty(context, "lastname", false, errorObject));
    member.setPassword(loadProperty(context, "password", true, errorObject));
    member.setGender(loadProperty(context, "gender", false, errorObject));
    return member;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractController#initProperties(java.util.Properties)
   */
  @Override
  public void initProperties(Properties properties) {
    successUrl = readProperty(SUCCESS_URL_PROP, null, true);
    failUrl = readProperty(FAIL_URL_PROP, null, true);
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
    def.setRoutes(new String[] { "/member/doRegister" });
    return def;
  }

  /**
   * Get the default properties for an implementation of StaticController
   * 
   * @return
   */
  public static Properties getDefaultProperties() {
    Properties json = new Properties();
    json.put(SUCCESS_URL_PROP, "/member/registerSuccess.html");
    json.put(FAIL_URL_PROP, "/member/register.html");
    return json;
  }

  private String loadProperty(RoutingContext context, String propName, boolean required, JsonObject errorObject) {
    String value = context.request().getParam(propName);
    // value = context.request().getFormAttribute(propName);
    if (required && (value == null || value.trim().isEmpty()))
      errorObject.put(propName + "Error", "parameter '" + propName + " is required");
    return value;
  }
}

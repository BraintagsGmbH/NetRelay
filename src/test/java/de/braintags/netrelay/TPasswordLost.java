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
package de.braintags.netrelay;

import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.DatastoreBaseTest;
import de.braintags.netrelay.controller.impl.ThymeleafTemplateController;
import de.braintags.netrelay.controller.impl.api.MailController;
import de.braintags.netrelay.controller.impl.authentication.PasswordLostCode;
import de.braintags.netrelay.controller.impl.authentication.PasswordLostController;
import de.braintags.netrelay.controller.impl.authentication.RegisterController;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.model.Member;
import de.braintags.netrelay.model.PasswordLostClaim;
import de.braintags.netrelay.model.RegisterClaim;
import de.braintags.netrelay.routing.RouterDefinition;
import de.braintags.netrelay.util.MultipartUtil;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TPasswordLost extends NetRelayBaseTest {
  /**
   * Comment for <code>MY_USERNAME</code>
   */
  private static final String MY_USERNAME = "myUsername";
  /**
   * Comment for <code>USER_BRAINTAGS_DE</code>
   */
  private static final String USER_BRAINTAGS_DE = "mremme@braintags.de";

  private static final String EMAIL_PROPERTY = "EMAIL";

  private static final String PASSWORD = "XXXXXXXXX";

  /**
   * Comment for <code>LOST_CONFIRM</code>
   */
  private static final String LOST_CONFIRM = "/customer/passwordLostConfirm";
  public static final String LOST_START = "/customer/passwordLost";

  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TPasswordLost.class);

  @Test
  public void testLost_EmailMissing(TestContext context) {
    try {
      DatastoreBaseTest.clearTable(context, RegisterClaim.class);
      DatastoreBaseTest.clearTable(context, Member.class);
      createMember(context);
      resetRoutes();
      MultipartUtil mu = new MultipartUtil();
      String url = LOST_START;

      testRequest(context, HttpMethod.POST, url, req -> {
        mu.finish(req);
      } , resp -> {
        LOGGER.info("RESPONSE: " + resp.content);
        LOGGER.info("HEADERS: " + resp.headers);
        context.assertTrue(resp.content.contains(PasswordLostCode.INVALID_ACTION.toString()),
            "The error code is not set: " + PasswordLostCode.INVALID_ACTION);
      } , 200, "OK", null);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  private void createMember(TestContext context) {
    Member member = new Member();
    member.setEmail(USER_BRAINTAGS_DE);
    member.setUserName(MY_USERNAME);
    member.setPassword(PASSWORD);
    DatastoreBaseTest.saveRecord(context, member);
  }

  @Test
  public void testPasswordLost(TestContext context) {
    String email = USER_BRAINTAGS_DE;
    try {
      DatastoreBaseTest.clearTable(context, PasswordLostClaim.class);
      DatastoreBaseTest.clearTable(context, Member.class);
      createMember(context);
      resetRoutes();
      performLostStart(context, email);
      validateNoMultipleRequests(context, email);
      // perform second time for checking duplications
      performLostStart(context, email);
      PasswordLostClaim claim = validateNoMultipleRequests(context, email);
      performConfirmation(context, claim);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * @param context
   * @param email
   * @throws Exception
   */
  private void performConfirmation(TestContext context, PasswordLostClaim claim) throws Exception {
    String url = LOST_CONFIRM + "?" + PasswordLostController.VALIDATION_ID_PARAM + "=" + claim.id;
    testRequest(context, HttpMethod.GET, url, req -> {
    } , resp -> {
      LOGGER.info("RESPONSE: " + resp.content);
      LOGGER.info("HEADERS: " + resp.headers);
      context.assertTrue(resp.content.contains(USER_BRAINTAGS_DE),
          "Did nont find email address in reply: " + USER_BRAINTAGS_DE);
    } , 200, "OK", null);

    IQuery<Member> query = netRelay.getDatastore().createQuery(Member.class);
    query.field("email").is(USER_BRAINTAGS_DE);
    Member member = (Member) DatastoreBaseTest.findFirst(context, query);
    context.assertNotNull(member, "Member was not created");
    context.assertEquals(MY_USERNAME, member.getUserName(), "username not set");
  }

  private MultipartUtil createFormRequest(String email) {
    MultipartUtil mu = new MultipartUtil();
    mu.addFormField(RegisterController.EMAIL_FIELD_NAME, email);
    return mu;
  }

  /**
   * @param context
   * @param email
   * @throws Exception
   */
  private void performLostStart(TestContext context, String email) throws Exception {
    MultipartUtil mu = createFormRequest(email);
    mu.addFormField(EMAIL_PROPERTY, USER_BRAINTAGS_DE);
    String url = LOST_START;

    testRequest(context, HttpMethod.POST, url, req -> {
      mu.finish(req);
    } , resp -> {
      LOGGER.info("RESPONSE: " + resp.content);
      LOGGER.info("HEADERS: " + resp.headers);
      context.assertNotNull(resp.headers.get("location"), "No location header set");
      context.assertTrue(resp.headers.get("location").contains("/customer/passwordLostSuccess.html"),
          "The success page isn't called");
    } , 302, "Found", null);
  }

  /**
   * validate, that there is only one active record with the email
   * 
   * @param context
   * @param email
   * @return
   */
  private PasswordLostClaim validateNoMultipleRequests(TestContext context, String email) {
    IQuery<PasswordLostClaim> query = netRelay.getDatastore().createQuery(PasswordLostClaim.class);
    query.field("email").is(email).field("active").is(true);
    List<?> recList = DatastoreBaseTest.findAll(context, query);
    context.assertEquals(1, recList.size(),
        "previous PasswordLostClaims are not deactivated ( > 1 ) OR PasswordLostClaim not written (0)");
    return (PasswordLostClaim) recList.get(0);
  }

  private void resetRoutes() throws Exception {
    RouterDefinition def = netRelay.getSettings().getRouterDefinitions()
        .getNamedDefinition(PasswordLostController.class.getSimpleName());
    def.setRoutes(new String[] { LOST_START, LOST_CONFIRM });
    def.getHandlerProperties().put(PasswordLostController.PW_LOST_FAIL_URL_PROP, "/customer/passwordLostError.html");
    def.getHandlerProperties().put(MailController.FROM_PARAM, TESTS_MAIL_FROM);
    def.getHandlerProperties().put(MailController.SUBJECT_PARAMETER, "Reset password");
    def.getHandlerProperties().put(MailController.TEMPLATE_PARAM, "/customer/passwordLostMail.html");
    def.getHandlerProperties().put(ThymeleafTemplateController.TEMPLATE_DIRECTORY_PROPERTY, "testTemplates");
    def.getHandlerProperties().put(PasswordLostController.AUTHENTICATABLE_CLASS_PROP, Member.class.getName());
    def.getHandlerProperties().put(PasswordLostController.PW_LOST_SUCCESS_URL_PROP,
        "/customer/passwordLostSuccess.html");
    def.getHandlerProperties().put(PasswordLostController.PW_RESET_SUCCESS_URL_PROP,
        "/customer/passwordLostConfirmSuccess.html");
    def.getHandlerProperties().put(PasswordLostController.PW_RESET_FAIL_URL_PROP, "/customer/passwordLostError.html");
    def.getHandlerProperties().put(PasswordLostController.EMAIL_FIELD_NAME_PROP, EMAIL_PROPERTY);

    netRelay.resetRoutes();
  }

  @Override
  protected void modifySettings(TestContext context, Settings settings) {
    super.modifySettings(context, settings);
    initMailClient(settings);
  }

}

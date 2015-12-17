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
package de.braintags.netrelay.controller.impl;

import java.util.Properties;

import de.braintags.netrelay.RequestUtil;
import de.braintags.netrelay.controller.impl.authentication.AuthenticationController;
import de.braintags.netrelay.model.Member;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.ext.web.RoutingContext;

/**
 * If a user is logged in, the propriate record is fetched from the datastore and stored as
 * {@link Member#CURRENT_USER_PROPERTY} in the context
 * 
 * @author Michael Remme
 * 
 */
public class CurrentMemberController extends AbstractController {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(CurrentMemberController.class);

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext context) {
    Member member = RequestUtil.getCurrentUser(context);
    if (member != null) {
      context.put(Member.CURRENT_USER_PROPERTY, member);
      LOGGER.info(member.getClass().getName());
      context.next();
    } else if (context.user() != null) {
      try {
        Class mapperClass = getMapperClass(context);
        RequestUtil.getCurrentUser(context, getNetRelay().getDatastore(), mapperClass, res -> {
          if (res.failed()) {
            context.fail(res.cause());
          } else {
            Member user = res.result();
            context.put(Member.CURRENT_USER_PROPERTY, user);
            RequestUtil.setCurrentUser(user, context);
            context.next();
          }
        });
      } catch (Exception e) {
        context.fail(e);
      }
    } else {
      context.next();
    }
  }

  private Class getMapperClass(RoutingContext context) {
    String mapperName = context.user().principal().getString(AuthenticationController.MAPPERNAME_IN_PRINCIPAL);
    if (mapperName == null) {
      throw new IllegalArgumentException("No mapper definition found in principal");
    }

    Class mapperClass = getNetRelay().getSettings().getMappingDefinitions().getMapperClass(mapperName);
    if (mapperClass == null) {
      throw new IllegalArgumentException("No MapperClass definition for: " + mapperName);
    }
    return mapperClass;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractController#initProperties(java.util.Properties)
   */
  @Override
  public void initProperties(Properties properties) {
  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName(CurrentMemberController.class.getSimpleName());
    def.setBlocking(false);
    def.setController(CurrentMemberController.class);
    def.setHandlerProperties(getDefaultProperties());
    def.setRoutes(new String[] {});
    return def;
  }

  /**
   * Get the default properties for an implementation of StaticController
   * 
   * @return
   */
  public static Properties getDefaultProperties() {
    Properties json = new Properties();
    return json;
  }
}

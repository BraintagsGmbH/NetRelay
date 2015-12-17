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

import de.braintags.netrelay.model.Member;
import io.vertx.ext.web.RoutingContext;

/**
 * If a user is logged in, the propriate record is fetched from the datastore and stored as
 * {@link Member#CURRENT_USER_PROPERTY} in the context
 * 
 * @author Michael Remme
 * 
 */
public class CurrentMemberController extends AbstractController {

  /**
   * 
   */
  public CurrentMemberController() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public void handle(RoutingContext context) {
    // if (context.user() != null) {
    // RequestUtil.getCurrentUser(context, getMongoClient(), FairyTaleVerticle.USER_COLLECTION_NAME, res -> {
    // if (res.failed()) {
    // context.fail(res.cause());
    // return;
    // } else {
    // Member user = res.result();
    // ApexUtil.setCurrentUser(user, context);
    // context.next();
    // }
    // });
    // } else
    // context.next();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractController#initProperties(java.util.Properties)
   */
  @Override
  public void initProperties(Properties properties) {
  }

}

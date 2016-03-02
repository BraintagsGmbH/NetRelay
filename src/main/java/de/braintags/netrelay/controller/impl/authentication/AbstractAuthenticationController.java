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

import java.util.Properties;

import de.braintags.netrelay.controller.impl.AbstractController;

/**
 * An abstract implementation of IController, which initializes an {@link AuthenticationController}
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractAuthenticationController extends AbstractController {

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractController#initProperties(java.util.Properties)
   */
  @Override
  public void initProperties(Properties properties) {
  }

}

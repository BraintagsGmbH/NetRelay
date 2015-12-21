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
package de.braintags.netrelay.init;

import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;

/**
 * The MailClientSettings are defining the parameters, by which a {@link MailClient} will be installed in NetRelay
 * 
 * @author Michael Remme
 * 
 */
public class MailClientSettings extends MailConfig {
  private boolean active = false;
  private String name = "NetRelay MailClient";

  /**
   * 
   */
  public MailClientSettings() {
  }

  /**
   * If active, the {@link MailClient} of NetRelay will be initialized
   * 
   * @return the active
   */
  public final boolean isActive() {
    return active;
  }

  /**
   * If active, the {@link MailClient} of NetRelay will be initialized
   * 
   * @param active
   *          the active to set
   */
  public final void setActive(boolean active) {
    this.active = active;
  }

  /**
   * The name of the service, under which the MailClient will be installed
   * 
   * @return the name
   */
  public final String getName() {
    return name;
  }

  /**
   * The name of the service, under which the MailClient will be installed
   * 
   * @param name
   *          the name to set
   */
  public final void setName(String name) {
    this.name = name;
  }

}

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

import java.net.URI;

import io.vertx.ext.mail.MailAttachment;

/**
 * An extension of MailAttachment, to enable storage of a content as URI
 * 
 * 
 * @author Michael Remme
 *
 */
public class UriMailAttachment extends MailAttachment {
  private URI uri;

  UriMailAttachment(URI uri) {
    this.uri = uri;
  }

  /**
   * The URI as content definition
   * 
   * @return the uri
   */
  public final URI getUri() {
    return uri;
  }

}
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
package de.braintags.netrelay.util;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@RunWith(VertxUnitRunner.class)
public class MockHttpServerRequestTest {
  String uriString = "http://localhost:8080/index.html";
  String uriString2 = "http://localhost:8080";

  /**
   * Test method for {@link de.braintags.netrelay.util.MockHttpServerRequest#uri()}.
   */
  @Test
  public void testUri(TestContext context) {
    try {
      MockHttpServerRequest request = new MockHttpServerRequest(new URI(uriString), new MockHttpServerResponse());
      context.assertNotNull(request.uri());
      context.assertEquals(uriString, request.uri());
    } catch (Exception e) {
      context.fail(e);
    }

  }

  /**
   * Test method for {@link de.braintags.netrelay.util.MockHttpServerRequest#path()}.
   */
  @Test
  public void testPath(TestContext context) {
    try {
      MockHttpServerRequest request = new MockHttpServerRequest(new URI(uriString), new MockHttpServerResponse());
      context.assertNotNull(request.uri());
      context.assertEquals("/index.html", request.path());
      request = new MockHttpServerRequest(new URI(uriString2), new MockHttpServerResponse());
      context.assertEquals("/", request.path());
    } catch (Exception e) {
      context.fail(e);
    }
  }

}

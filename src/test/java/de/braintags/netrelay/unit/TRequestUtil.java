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
package de.braintags.netrelay.unit;

import org.junit.Test;

import de.braintags.vertx.util.request.RequestUtil;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TRequestUtil extends NetRelayBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TRequestUtil.class);

  @Test
  public void testCleanPath(TestContext context) {
    String path = "first/second/third/fourth";
    String result = RequestUtil.cleanPathElement("second", path);
    context.assertEquals("first/third/fourth", result);
    result = RequestUtil.cleanPathElement("first", path);
    context.assertEquals("second/third/fourth", result);
    result = RequestUtil.cleanPathElement("fourth", path);
    context.assertEquals("first/second/third", result);

    path = "/first/second/third/fourth";
    result = RequestUtil.cleanPathElement("second", path);
    context.assertEquals("/first/third/fourth", result);
    result = RequestUtil.cleanPathElement("first", path);
    context.assertEquals("/second/third/fourth", result);
    result = RequestUtil.cleanPathElement("fourth", path);
    context.assertEquals("/first/second/third", result);

    path = "first/second/third/fourth/";
    result = RequestUtil.cleanPathElement("second", path);
    context.assertEquals("first/third/fourth/", result);
    result = RequestUtil.cleanPathElement("first", path);
    context.assertEquals("second/third/fourth/", result);
    result = RequestUtil.cleanPathElement("fourth", path);
    context.assertEquals("first/second/third/", result);

    path = "first/second/third/firstfourth/";
    result = RequestUtil.cleanPathElement("first", path);
    context.assertEquals("second/third/firstfourth/", result);
  }

  @Test
  public void testSplitPath(TestContext context) {
    splitPath(context, "/test/at/index.html", "/test/at/", "index");
    splitPath(context, "/test/at/", "/test/at/", "index");
    splitPath(context, "/index.html", "/", "index");
    splitPath(context, "/", "/", "index");
    splitPath(context, "test/at/index.html", "test/at/", "index");
    try {
      splitPath(context, "", "/", "index");
      context.fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // this is expected
    }
  }

  private void splitPath(TestContext context, String url, String expectedPath, String expectedFile) {
    String[] result = RequestUtil.splitPathFile(url);
    context.assertEquals(expectedPath, result[0]);
    context.assertEquals(expectedFile, result[1]);
  }
}

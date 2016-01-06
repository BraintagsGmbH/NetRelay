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

import org.junit.Test;

import de.braintags.netrelay.mapper.TestCustomer;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TRequestUtil extends NetRelayBaseTest {

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
  public void testCustomer(TestContext context) {
    netRelay.getDatastore().createQuery(TestCustomer.class);
  }

}

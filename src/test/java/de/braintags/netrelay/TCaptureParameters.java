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

import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.routing.CaptureCollection;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;

/**
 * TEst the functions of {@link CaptureCollection}
 * 
 * @author Michael Remme
 * 
 */
public class TCaptureParameters extends AbstractCaptureParameterTest {

  @Test
  public void testParameters1(TestContext context) throws Exception {
    CaptureTestController.resolvedCaptureCollections = null;
    testRequest(context, HttpMethod.GET, "/products/nase/12/tuEs/detail.html", 200, "OK");
    context.assertNotNull(CaptureTestController.resolvedCaptureCollections);
    context.assertEquals(1, CaptureTestController.resolvedCaptureCollections.size());
    assertValues(context, 0, "nase", "tuEs", "12");
    context.assertEquals("/products/detail.html", CaptureTestController.cleanedPath);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelayBaseTest#modifySettings(de.braintags.netrelay.init.Settings)
   */
  @Override
  protected void modifySettings(TestContext context, Settings settings) {
    super.modifySettings(context, settings);
    settings.getRouterDefinitions().add(0,
        defineRouterDefinition(CaptureTestController.class, "/products/:entity/:ID/:action/detail.html"));
    boolean exceptionRaised = false;
    try {
      // capture parameters and asterisk are not possible
      settings.getRouterDefinitions().add(0,
          defineRouterDefinition(CaptureTestController.class, "/animal/:entity/:ID/:action/*"));
    } catch (IllegalArgumentException e) {
      exceptionRaised = true;
    }
    if (!exceptionRaised) {
      context.fail("Expected an Exception, which wasn't thrown");
    }
  }

}

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

import de.braintags.netrelay.controller.impl.AbstractCaptureController.CaptureMap;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.routing.CaptureCollection;
import de.braintags.netrelay.routing.CaptureDefinition;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;

/**
 * TEst the functions of {@link CaptureCollection}
 * 
 * @author Michael Remme
 * 
 */
public class TCaptureParameters extends NetRelayBaseTest {

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
    settings.getRouterDefinitions().add(0, defineRouterDefinition("/products/:entity/:ID/:action/detail.html"));
    boolean exceptionRaised = false;
    try {
      // capture parameters and asterisk are not possible
      settings.getRouterDefinitions().add(0, defineRouterDefinition("/animal/:entity/:ID/:action/*"));
    } catch (IllegalArgumentException e) {
      exceptionRaised = true;
    }
    if (!exceptionRaised) {
      context.fail("Expected an Exception, which wasn't thrown");
    }
  }

  private void assertValues(TestContext context, int position, String mapper, String action, String id) {
    CaptureMap cm = CaptureTestController.resolvedCaptureCollections.get(position);
    context.assertEquals(mapper, cm.get(CaptureTestController.MAPPER_KEY));
    context.assertEquals(id, cm.get(CaptureTestController.ID_KEY));
    context.assertEquals(action, cm.get(CaptureTestController.ACTION_KEY));
  }

  private RouterDefinition defineRouterDefinition(String route) {
    RouterDefinition rd = new RouterDefinition();
    rd.setName(CaptureTestController.class.getSimpleName());
    rd.setController(CaptureTestController.class);
    rd.setRoutes(new String[] { route });
    rd.setCaptureCollection(createDefaultCaptureCollection());
    return rd;
  }

  private CaptureCollection[] createDefaultCaptureCollection() {
    CaptureDefinition[] defs = new CaptureDefinition[3];
    defs[0] = new CaptureDefinition("entity", CaptureTestController.MAPPER_KEY, false);
    defs[1] = new CaptureDefinition("ID", CaptureTestController.ID_KEY, false);
    defs[2] = new CaptureDefinition("action", CaptureTestController.ACTION_KEY, false);
    CaptureCollection collection = new CaptureCollection();
    collection.setCaptureDefinitions(defs);
    CaptureCollection[] cc = new CaptureCollection[] { collection };
    return cc;
  }
}

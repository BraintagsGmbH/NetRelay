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
  public void testParameters(TestContext context) throws Exception {
    CaptureTestController.resolvedCaptureCollections = null;
    testRequest(context, HttpMethod.GET, "/products/nase/12/tuEs", 200, "OK");
    context.assertEquals(1, CaptureTestController.resolvedCaptureCollections.size());
    CaptureMap cm = CaptureTestController.resolvedCaptureCollections.get(0);
    context.assertEquals("nase", cm.get(CaptureTestController.MAPPER_KEY));
    context.assertEquals("12", cm.get(CaptureTestController.ID_KEY));
    context.assertEquals("tuEs", cm.get(CaptureTestController.ACTION_KEY));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.NetRelayBaseTest#modifySettings(de.braintags.netrelay.init.Settings)
   */
  @Override
  protected void modifySettings(Settings settings) {
    super.modifySettings(settings);
    RouterDefinition rd = new RouterDefinition();
    rd.setName(CaptureTestController.class.getSimpleName());
    rd.setController(CaptureTestController.class);
    rd.setRoutes(new String[] { "/products/:entity/:ID/:action" });
    CaptureDefinition[] defs = new CaptureDefinition[3];
    defs[0] = new CaptureDefinition("entity", CaptureTestController.MAPPER_KEY, false);
    defs[1] = new CaptureDefinition("ID", CaptureTestController.ID_KEY, false);
    defs[2] = new CaptureDefinition("action", CaptureTestController.ACTION_KEY, false);

    settings.getRouterDefinitions().add(rd);
  }
}

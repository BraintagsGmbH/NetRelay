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

import de.braintags.netrelay.controller.CaptureTestController;
import de.braintags.netrelay.controller.impl.AbstractCaptureController.CaptureMap;
import de.braintags.netrelay.routing.CaptureCollection;
import de.braintags.netrelay.routing.CaptureDefinition;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractCaptureParameterTest extends NetRelayBaseTest {

  protected void assertValues(TestContext context, int position, String mapper, String action, String id) {
    CaptureMap cm = CaptureTestController.resolvedCaptureCollections.get(position);
    context.assertEquals(mapper, cm.get(CaptureTestController.MAPPER_KEY));
    context.assertEquals(id, cm.get(CaptureTestController.ID_KEY));
    context.assertEquals(action, cm.get(CaptureTestController.ACTION_KEY));
  }

  @Override
  protected RouterDefinition defineRouterDefinition(Class controllerClass, String route) {
    RouterDefinition rd = super.defineRouterDefinition(controllerClass, route);
    rd.setCaptureCollection(createDefaultCaptureCollection());
    return rd;
  }

  protected CaptureCollection[] createDefaultCaptureCollection() {
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

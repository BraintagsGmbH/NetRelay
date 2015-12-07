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

import de.braintags.netrelay.controller.impl.persistence.PersistenceController;
import de.braintags.netrelay.init.Settings;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TPersistenceController extends AbstractCaptureParameterTest {

  @Test
  public void testInsert(TestContext context) throws Exception {
    CaptureTestController.resolvedCaptureCollections = null;
    testRequest(context, HttpMethod.GET, "/products/nase/12/INSERT/detail.html", 200, "OK");
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
        defineRouterDefinition(PersistenceController.class, "/products/:entity/:ID/:action/detail.html"));
  }

}

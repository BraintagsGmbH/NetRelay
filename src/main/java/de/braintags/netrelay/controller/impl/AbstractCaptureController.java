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
package de.braintags.netrelay.controller.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.braintags.io.vertx.pojomapper.exception.ParameterRequiredException;
import de.braintags.netrelay.controller.IController;
import de.braintags.netrelay.routing.CaptureCollection;
import de.braintags.netrelay.routing.CaptureDefinition;
import io.vertx.ext.web.RoutingContext;

/**
 * An abstract implementation of {@link IController}, which uses defined {@link CaptureCollection}
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractCaptureController extends AbstractController {

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public final void handle(RoutingContext context) {
    List<CaptureMap> resolvedCaptureCollections = resolveCaptureCollections(context);
    handle(context, resolvedCaptureCollections);
  }

  /**
   * Handle the request by using the resolved parameters from the current request
   * 
   * @param context
   *          the current {@link RoutingContext}
   * @param resolvedCaptureCollections
   *          the parameters, which are resolved from the current request
   *          into the parameter names like they are supported from the current instance
   */
  protected abstract void handle(RoutingContext context, List<CaptureMap> resolvedCaptureCollections);

  /**
   * Resolves all defined {@link CaptureCollection} from the current request.
   * The result is per defined CaptureCollection an instance of {@link CaptureMap},
   * where the key is the key of the current IController and the value is the corresponding
   * value from the request
   * 
   * @param event
   *          the current routing event with the request
   */
  protected List<CaptureMap> resolveCaptureCollections(RoutingContext event) {
    List<CaptureMap> resolvedCaptureCollections = new ArrayList<>();
    if (getCaptureCollections() != null) {
      for (CaptureCollection collection : getCaptureCollections()) {
        resolvedCaptureCollections.add(resolveCollection(event, collection));
      }
    }
    return resolvedCaptureCollections;
  }

  protected CaptureMap resolveCollection(RoutingContext event, CaptureCollection collection) {
    CaptureMap csMap = new CaptureMap();
    for (CaptureDefinition def : collection.getCaptureDefinitions()) {
      resolveCaptureDefinition(event, csMap, def);
    }
    return csMap;
  }

  protected void resolveCaptureDefinition(RoutingContext event, CaptureMap csMap, CaptureDefinition def) {
    String value = event.request().getParam(def.getCaptureName());
    if (value == null && def.isRequired()) {
      throw new ParameterRequiredException(def.getCaptureName());
    } else if (value != null) {
      csMap.put(def.getControllerKey(), value);
    }
  }

  /**
   * The CaptureMap contains the resolved key / values from a request for one action to be performed.
   * The key of the map is the key of the {@link IController} and the value is the corresponding value of the current
   * request
   * 
   * 
   * @author Michael Remme
   *
   */
  public class CaptureMap extends HashMap<String, String> {
  }

}

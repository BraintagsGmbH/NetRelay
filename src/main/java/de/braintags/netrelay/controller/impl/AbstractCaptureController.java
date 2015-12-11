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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import de.braintags.io.vertx.pojomapper.exception.ParameterRequiredException;
import de.braintags.netrelay.RequestUtil;
import de.braintags.netrelay.controller.IController;
import de.braintags.netrelay.routing.CaptureCollection;
import de.braintags.netrelay.routing.CaptureDefinition;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * An abstract implementation of {@link IController}, which uses defined {@link CaptureCollection}.
 * To use it, you will define a route like "/products/:entity/:ID/detail.html".
 * Additionally you will define a CaptureCollection:
 * 
 * <pre>
 * CaptureCollection collection = new CaptureCollection();
 * CaptureDefinition[] defs = new CaptureDefinition[3];
 * defs[0] = new CaptureDefinition("entity", CaptureTestController.MAPPER_KEY, false);
 * defs[1] = new CaptureDefinition("ID", CaptureTestController.ID_KEY, false);
 * defs[2] = new CaptureDefinition("action", CaptureTestController.ACTION_KEY, false);
 * collection.setCaptureDefinitions(defs);
 * </pre>
 * 
 * Inside the CaptureCollection you are defining the "translator", which capture means which property of
 * the AbstractCaptureController.
 * When the property {@link #REROUTE_PROPERTY} is set to true, then the controller will call a reroute to
 * a cleaned URI. The new URI is determined by evaluation of the properties {@link #DESTINATION_PATH_PROPERTY}
 * and {@link #AUTO_CLEAN_PATH_PROPERTY}
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractCaptureController extends AbstractController {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractCaptureController.class);

  /**
   * If this property is set to true, then a reroute to a cleaned URI is performed
   */
  public static final String REROUTE_PROPERTY = "reroute";

  /**
   * If this property is set to true, then the controller removes the dynamic parts of the URI to generate the clean URI
   */
  public static final String AUTO_CLEAN_PATH_PROPERTY = "cleanPath";

  /**
   * By this property you are able to set the destination for a rerouting manually
   */
  public static final String DESTINATION_PATH_PROPERTY = "destinationPath";

  private static final String ERROR_MESSAGE = String.format(
      "Property %s is set to true, but %s is not set and %s is false", REROUTE_PROPERTY, DESTINATION_PATH_PROPERTY,
      AUTO_CLEAN_PATH_PROPERTY);

  private String destinationTemplate;
  private boolean doReroute = true;
  private boolean doAutoCleanPath = true;

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.core.Handler#handle(java.lang.Object)
   */
  @Override
  public final void handle(RoutingContext context) {
    List<CaptureMap> resolvedCaptureCollections = resolveCaptureCollections(context);
    handle(context, resolvedCaptureCollections, result -> {
      if (result.failed()) {
        context.fail(result.cause());
      } else {
        handleReroute(context, resolvedCaptureCollections);
      }
    });
  }

  /**
   * If reroute is true, then a reroute to the normalized path is called, otherwise {@link RoutingContext#next()} is
   * called
   * 
   * @param context
   *          the context of the current request
   * @param resolvedCaptureCollections
   *          the resolved data to clean the path
   */
  protected void handleReroute(RoutingContext context, List<CaptureMap> resolvedCaptureCollections) {
    if (doReroute) {
      String cleanedPath = cleanPath(context, resolvedCaptureCollections);
      LOGGER.info("rerouting to " + cleanedPath);
      context.reroute(cleanedPath);
    } else {
      context.next();
    }
  }

  protected String cleanPath(RoutingContext context, List<CaptureMap> resolvedCaptureCollections) {
    if (destinationTemplate != null) {
      return destinationTemplate;
    } else if (doAutoCleanPath) {
      String path = context.normalisedPath();
      for (CaptureMap cm : resolvedCaptureCollections) {
        path = cleanPath(cm, path);
      }
      return path;
    } else {
      throw new UnsupportedOperationException(ERROR_MESSAGE);
    }
  }

  protected String cleanPath(CaptureMap map, String path) {
    Iterator<String> values = map.values().iterator();
    while (values.hasNext()) {
      String value = values.next();
      path = RequestUtil.cleanPathElement(value, path);
    }
    return path;
  }

  /**
   * Handle the request by using the resolved parameters from the current request
   * 
   * @param context
   *          the current {@link RoutingContext}
   * @param resolvedCaptureCollections
   *          the parameters, which are resolved from the current request
   *          into the parameter names like they are supported from the current instance
   * @param handler
   *          the handler to be informed if finished
   */
  protected abstract void handle(RoutingContext context, List<CaptureMap> resolvedCaptureCollections,
      Handler<AsyncResult<Void>> handler);

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
  @SuppressWarnings("serial")
  public class CaptureMap extends HashMap<String, String> {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractController#initProperties(java.util.Properties)
   */
  @Override
  public final void initProperties(Properties properties) {
    destinationTemplate = (String) properties.get(DESTINATION_PATH_PROPERTY);
    doReroute = Boolean.valueOf((String) properties.getOrDefault(REROUTE_PROPERTY, String.valueOf(doReroute)));
    doAutoCleanPath = Boolean
        .valueOf((String) properties.getOrDefault(AUTO_CLEAN_PATH_PROPERTY, String.valueOf(doAutoCleanPath)));
    if (doReroute && !doAutoCleanPath && destinationTemplate == null) {
      throw new IllegalArgumentException(ERROR_MESSAGE);
    }
    internalInitProperties(properties);
  }

  /**
   * This method is called after the properties about rerouting are examined
   * 
   * @param properties
   */
  protected abstract void internalInitProperties(Properties properties);

  /**
   * @return the doReroute
   */
  public final boolean isDoReroute() {
    return doReroute;
  }
}

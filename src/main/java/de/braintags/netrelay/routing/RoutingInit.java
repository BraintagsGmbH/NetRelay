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
package de.braintags.netrelay.routing;

import java.util.ArrayList;
import java.util.List;

import de.braintags.netrelay.NetRelay;
import de.braintags.netrelay.controller.IController;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

/**
 * Utility class for initialization of Routing
 * 
 * @author mremme
 * 
 */
public class RoutingInit {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(RoutingInit.class);

  /**
   * 
   */
  private RoutingInit() {
  }

  public static void initRoutingDefinition(Vertx vertx, NetRelay netRelay, Router router, RouterDefinition def)
      throws Exception {
    if (def.isActive()) {
      if (def.isFailureDefinition()) {
        initFailureDefinition(vertx, netRelay, router, def);
      } else {
        initRegularDefinition(vertx, netRelay, router, def);
      }
    }
  }

  private static void initRegularDefinition(Vertx vertx, NetRelay netRelay, Router router, RouterDefinition def)
      throws Exception {
    IController controller = def.instantiateController(vertx, netRelay);
    List<Route> routes = getRoutes(router, def);
    for (Route route : routes) {
      route.handler(controller);
    }
  }

  private static void initFailureDefinition(Vertx vertx, NetRelay netRelay, Router router, RouterDefinition def)
      throws Exception {
    LOGGER.info("init Failure Definition: " + def.getName());
    IController controller = def.instantiateController(vertx, netRelay);
    List<Route> routes = getRoutes(router, def);
    for (Route route : routes) {
      route.failureHandler(controller);
    }
  }

  private static List<Route> getRoutes(Router router, RouterDefinition def) {
    List<Route> returnList = new ArrayList<>();
    if (def.getRoutes() == null && def.getHttpMethod() == null) {
      returnList.add(router.route());
    } else if (def.getHttpMethod() == null) {
      for (String route : def.getRoutes()) {
        returnList.add(router.route(route));
      }
    } else {
      HttpMethod method = def.getHttpMethod();
      for (String route : def.getRoutes()) {
        returnList.add(router.route(method, route));
      }
    }
    return returnList;
  }
}

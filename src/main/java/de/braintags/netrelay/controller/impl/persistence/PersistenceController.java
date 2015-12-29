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
package de.braintags.netrelay.controller.impl.persistence;

import java.util.List;
import java.util.Properties;

import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import de.braintags.netrelay.controller.Action;
import de.braintags.netrelay.controller.impl.AbstractCaptureController;
import de.braintags.netrelay.mapping.NetRelayMapperFactory;
import de.braintags.netrelay.routing.CaptureCollection;
import de.braintags.netrelay.routing.CaptureDefinition;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * The PersistenceController is the frame, which defines the logics of a request, so that mapper objects can be fetched
 * from a datastore, inserted or updated by request or form parameters and deleted from the datastore.
 * 
 * <br/>
 * <br/>
 * possible paramters are:
 * <br/>
 * {@value #MAPPER_KEY}<br/>
 * {@value #ID_KEY}<br/>
 * {@value #ACTION_KEY}<br/>
 * {@value #UPLOAD_DIRECTORY_PROP}<br/>
 * 
 * Further parameters {@link AbstractCaptureController}
 * 
 * @author Michael Remme
 * 
 */
public class PersistenceController extends AbstractCaptureController {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(PersistenceController.class);

  /**
   * The name of the property, which defines the directory, where uploaded files are transferred into
   */
  public static final String UPLOAD_DIRECTORY_PROP = "uploadDirectory";

  /**
   * The name of a the property in the request, which specifies the mapper
   */
  public static final String MAPPER_KEY = "mapper";
  /**
   * The name of the property in the request, which specifies the ID of a record
   */
  public static final String ID_KEY = "ID";
  /**
   * The name of the property in the request, which specifies the action
   */
  public static final String ACTION_KEY = "action";

  private IMapperFactory mapperFactory;

  private DisplayAction displayAction;
  private InsertAction insertAction;
  private UpdateAction updateAction;
  private DeleteAction deleteAction;
  private NoneAction noneAction;

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractCaptureController#handle(io.vertx.ext.web.RoutingContext,
   * java.util.List)
   */
  @Override
  protected void handle(RoutingContext context, List<CaptureMap> resolvedCaptureCollections,
      Handler<AsyncResult<Void>> handler) {
    ErrorObject<Void> err = new ErrorObject<>(handler);
    CounterObject co = new CounterObject(resolvedCaptureCollections.size());

    for (CaptureMap map : resolvedCaptureCollections) {
      handle(context, map, result -> {
        if (result.failed()) {
          err.setThrowable(result.cause());
        } else {
          if (co.reduce()) {
            handler.handle(Future.succeededFuture());
          }
        }
      });
      if (err.isError()) {
        return;
      }
    }
  }

  private void handle(RoutingContext context, CaptureMap map, Handler<AsyncResult<Void>> handler) {
    AbstractAction action = resolveAction(map);
    String mapperName = map.get(PersistenceController.MAPPER_KEY);

    LOGGER.info(String.format("handling action %s on mapper %s", action, mapperName));
    action.handle(mapperName, context, map, handler);
  }

  private AbstractAction resolveAction(CaptureMap map) {
    String actionKey = map.get(ACTION_KEY);
    Action action = actionKey == null ? Action.DISPLAY : Action.valueOf(actionKey);
    switch (action) {
    case DISPLAY:
      return displayAction;

    case INSERT:
      return insertAction;

    case UPDATE:
      return updateAction;

    case DELETE:
      return deleteAction;

    case NONE:
      return noneAction;

    default:
      throw new UnsupportedOperationException("unknown action: " + action);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractCaptureController#internalInitProperties(java.util.Properties)
   */
  @Override
  protected void internalInitProperties(Properties properties) {
    displayAction = new DisplayAction(this);
    insertAction = new InsertAction(this);
    updateAction = new UpdateAction(this);
    deleteAction = new DeleteAction(this);
    noneAction = new NoneAction(this);
    mapperFactory = new NetRelayMapperFactory(getNetRelay());
  }

  /**
   * Creates a default definition for the current instance
   * 
   * @return
   */
  public static RouterDefinition createDefaultRouterDefinition() {
    RouterDefinition def = new RouterDefinition();
    def.setName(PersistenceController.class.getSimpleName());
    def.setBlocking(true);
    def.setController(PersistenceController.class);
    def.setHandlerProperties(getDefaultProperties());
    def.setRoutes(new String[] { "/persistenceController/:entity/:ID/:action/read.html" });
    def.setCaptureCollection(createDefaultCaptureCollection());
    return def;
  }

  private static CaptureCollection[] createDefaultCaptureCollection() {
    CaptureDefinition[] defs = new CaptureDefinition[3];
    defs[0] = new CaptureDefinition("entity", PersistenceController.MAPPER_KEY, false);
    defs[1] = new CaptureDefinition("ID", PersistenceController.ID_KEY, false);
    defs[2] = new CaptureDefinition("action", PersistenceController.ACTION_KEY, false);
    CaptureCollection collection = new CaptureCollection();
    collection.setCaptureDefinitions(defs);
    CaptureCollection[] cc = new CaptureCollection[] { collection };
    return cc;
  }

  /**
   * Get the default properties for an implementation
   * 
   * @return
   */
  public static Properties getDefaultProperties() {
    Properties json = new Properties();
    json.put(REROUTE_PROPERTY, "false");
    json.put(AUTO_CLEAN_PATH_PROPERTY, "false");
    return json;
  }

  /**
   * @return the mapperFactory
   */
  public final IMapperFactory getMapperFactory() {
    return mapperFactory;
  }
}

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
import de.braintags.io.vertx.util.exception.InitException;
import de.braintags.netrelay.controller.Action;
import de.braintags.netrelay.controller.impl.AbstractCaptureController;
import de.braintags.netrelay.routing.CaptureCollection;
import de.braintags.netrelay.routing.CaptureDefinition;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.file.FileSystem;
import io.vertx.ext.web.RoutingContext;

/**
 * The PersistenceController is the frame, which defines the logics of a request, so that mapper objects can be fetched
 * from a datastore, inserted or updated by request or form parameters and deleted from the datastore.
 * 
 * <br/>
 * <br/>
 * possible paramters are:
 * <br/>
 * {@value #MAPPER_CAPTURE_KEY} - the name of the parameter, which specifies the mapper to be used<br/>
 * {@value #ID_CAPTURE_KEY} - the name of the parameter, which specifies the id of a record<br/>
 * {@value #ACTION_CAPTURE_KEY} - possible actions are defined by {@link Action}<br/>
 * {@value #UPLOAD_DIRECTORY_PROP} - The name of the property, which defines the directory, where uploaded files are
 * transferred into. This can be "webroot/images/" for instance<br/>
 * {@value #UPLOAD_RELATIVE_PATH_PROP} - The name of the property, which defines the relative path for uploaded files.
 * If the {@link #UPLOAD_DIRECTORY_PROP} is defined as "webroot/images/" for instance, then the relative path here could
 * be "images/"<br/>
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
   * The name of the property, which defines the directory, where uploaded files are transferred into.
   * This can be "webroot/images/" for instance
   */
  public static final String UPLOAD_DIRECTORY_PROP = "uploadDirectory";

  /**
   * The name of the property, which defines the relative path for uploaded files. If the {@link #UPLOAD_DIRECTORY_PROP}
   * is defined as "webroot/images/" for instance, then the relative path here could be "images/"
   */
  public static final String UPLOAD_RELATIVE_PATH_PROP = "uploadRelativePath";

  /**
   * The name of a the property in the request, which specifies the mapper
   */
  public static final String MAPPER_CAPTURE_KEY = "mapper";

  /**
   * The name of the property in the request, which specifies the ID of a record
   */
  public static final String ID_CAPTURE_KEY = "ID";

  /**
   * The name of the property in the request, which defines the number of records of a selection. This property is used
   * in case of action display, when a list of records shall be displayed
   */
  public static final String SELECTION_SIZE_CAPTURE_KEY = "selectionSize";

  /**
   * The name of the property in the request, which defines the start of a selection
   */
  public static final String SELECTION_START_CAPTURE_KEY = "selectionStart";

  /**
   * The name of the property in the request, which defines the fields to sort a selection
   */
  public static final String ORDERBY_CAPTURE_KEY = "orderBy";

  /**
   * The name of the property in the request, which specifies the action
   */
  public static final String ACTION_CAPTURE_KEY = "action";

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
    CounterObject<Void> co = new CounterObject<>(resolvedCaptureCollections.size(), handler);

    for (CaptureMap map : resolvedCaptureCollections) {
      handle(context, map, result -> {
        if (result.failed()) {
          co.setThrowable(result.cause());
        } else {
          if (co.reduce()) {
            handler.handle(Future.succeededFuture());
          }
        }
      });
      if (co.isError()) {
        return;
      }
    }
  }

  private void handle(RoutingContext context, CaptureMap map, Handler<AsyncResult<Void>> handler) {
    AbstractAction action = resolveAction(map);
    String mapperName = map.get(PersistenceController.MAPPER_CAPTURE_KEY);

    LOGGER.info(String.format("handling action %s on mapper %s", action, mapperName));
    action.handle(mapperName, context, map, handler);
  }

  private AbstractAction resolveAction(CaptureMap map) {
    String actionKey = map.get(ACTION_CAPTURE_KEY);
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
    mapperFactory = getNetRelay().getNetRelayMapperFactory();
    String upDir = readProperty(PersistenceController.UPLOAD_DIRECTORY_PROP, null, true);
    FileSystem fs = getVertx().fileSystem();
    if (!fs.existsBlocking(upDir)) {
      fs.mkdirsBlocking(upDir);
      if (!fs.existsBlocking(upDir)) {
        throw new InitException("could not create directory " + upDir);
      } else {
        LOGGER.info("Upload directory created: " + upDir);
      }
    }
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
    defs[0] = new CaptureDefinition("entity", PersistenceController.MAPPER_CAPTURE_KEY, false);
    defs[1] = new CaptureDefinition("ID", PersistenceController.ID_CAPTURE_KEY, false);
    defs[2] = new CaptureDefinition("action", PersistenceController.ACTION_CAPTURE_KEY, false);
    defs[3] = new CaptureDefinition("selectionSize", PersistenceController.SELECTION_SIZE_CAPTURE_KEY, false);
    defs[4] = new CaptureDefinition("selectionStart", PersistenceController.SELECTION_START_CAPTURE_KEY, false);
    defs[5] = new CaptureDefinition("orderBy", PersistenceController.ORDERBY_CAPTURE_KEY, false);

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
    json.put(REROUTE_PROPERTY, "true");
    json.put(AUTO_CLEAN_PATH_PROPERTY, "true");
    json.put(UPLOAD_DIRECTORY_PROP, "webroot/images/");
    json.put(UPLOAD_RELATIVE_PATH_PROP, "images/");
    return json;
  }

  /**
   * @return the mapperFactory
   */
  public final IMapperFactory getMapperFactory() {
    return mapperFactory;
  }
}

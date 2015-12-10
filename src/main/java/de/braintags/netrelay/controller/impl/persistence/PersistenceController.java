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

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.exception.InitException;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.netrelay.controller.Action;
import de.braintags.netrelay.controller.impl.AbstractCaptureController;
import de.braintags.netrelay.exception.NoSuchMapperException;
import de.braintags.netrelay.init.MappingDefinitions;
import de.braintags.netrelay.init.Settings;
import de.braintags.netrelay.mapping.NetRelayMapperFactory;
import de.braintags.netrelay.routing.CaptureCollection;
import de.braintags.netrelay.routing.CaptureDefinition;
import de.braintags.netrelay.routing.RouterDefinition;
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class PersistenceController extends AbstractCaptureController {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(PersistenceController.class);

  /**
   * Get the name of the property, by which the class of the {@link IMapperFactory} can be defined.
   */
  public static final String MAPPERFACTORY_PROP = "mapperfactory";

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

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractCaptureController#handle(io.vertx.ext.web.RoutingContext,
   * java.util.List)
   */
  @Override
  protected void handle(RoutingContext context, List<CaptureMap> resolvedCaptureCollections) {
    for (CaptureMap map : resolvedCaptureCollections) {
      handle(context, map);
    }
  }

  /**
   * Retrive the {@link IMapper} which is specified by the given mapperName.
   * 
   * @param mapperName
   *          the name of the mapper. This name mus exist as definition inside the {@link MappingDefinitions} of the
   *          {@link Settings}
   * @return a mapper from the internal {@link IMapperFactory}
   */
  IMapper getMapper(String mapperName) {
    Class mapperClass = getNetRelay().getSettings().getMappingDefinitions().getMapperClass(mapperName);
    if (mapperClass == null) {
      throw new NoSuchMapperException(mapperName);
    }
    return mapperFactory.getMapper(mapperClass);
  }

  private void handle(RoutingContext context, CaptureMap map) {
    AbstractAction action = resolveAction(map);
    String mapperName = map.get(PersistenceController.MAPPER_KEY);
    LOGGER.info(String.format("handling action %s on mapper %s", action, mapperName));
    IMapper mapper = getMapper(mapperName);
    IDataStore datastore = getNetRelay().getDatastore();
    action.handle(datastore, mapperName, mapper, context, map, result -> {
      if (result.failed()) {
        context.fail(result.cause());
      } else {
        context.next();
      }
    });

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
    String mfName = properties.getProperty(MAPPERFACTORY_PROP, NetRelayMapperFactory.class.getName());
    try {
      mapperFactory = (IMapperFactory) Class.forName(mfName).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new InitException(e);
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
    def.setBlocking(false);
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
    json.put(REROUTE_PROPERTY, "true");
    json.put(AUTO_CLEAN_PATH_PROPERTY, "true");
    json.put(MAPPERFACTORY_PROP, NetRelayMapperFactory.class.getName());
    return json;
  }
}

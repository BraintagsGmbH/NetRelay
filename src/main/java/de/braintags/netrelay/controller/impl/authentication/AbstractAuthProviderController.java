/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.netrelay.controller.impl.authentication;

import java.util.Properties;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.netrelay.controller.impl.AbstractController;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.mongo.HashSaltStyle;
import io.vertx.ext.auth.mongo.MongoAuth;

/**
 * An abstract implementation of IController, which initializes an {@link AuthProvider} to be used to perform
 * authentication and authorization in extending controllers
 * 
 * Config-Parameter:<br/>
 * possible parameters, which are read from the configuration
 * <UL>
 * <LI>{@value #AUTH_PROVIDER_PROP}
 * <LI>{@value #MAPPERNAME_IN_PRINCIPAL}
 * <LI>for {@link MongoAuth}, specific parameters for MongoAuth can be added, like
 * {@link MongoAuth#PROPERTY_COLLECTION_NAME}
 * </UL>
 * <br>
 * 
 * Request-Parameter:<br/>
 * possible parameters, which are read from a request
 * <UL>
 * <LI>none
 * </UL>
 * <br/>
 * 
 * Result-Parameter:<br/>
 * possible paramters, which will be placed into the context
 * <UL>
 * <LI>none
 * </UL>
 * <br/>
 * 
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractAuthProviderController extends AbstractController {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractAuthProviderController.class);

  /**
   * Used as possible value for property {@link #AUTH_PROVIDER_PROP} and references to an authentivation provider
   * connected to a mongo db
   */
  public static final String AUTH_PROVIDER_MONGO = "MongoAuth";

  /**
   * The name of the key, which is used, to store the name of the mapper in the {@link User#principal()}
   */
  public static final String MAPPERNAME_IN_PRINCIPAL = "mapper";

  /**
   * Defines the name of the {@link AuthProvider} to be used. Currently {@link #AUTH_PROVIDER_MONGO} is supported
   */
  public static final String AUTH_PROVIDER_PROP = "authProvider";

  private AuthProvider authProvider;

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.netrelay.controller.impl.AbstractController#initProperties(java.util.Properties)
   */
  @Override
  public void initProperties(Properties properties) {
    this.authProvider = createAuthProvider(properties);
  }

  /**
   * Get the initialized instance of {@link AuthProvider}
   * 
   * @return the {@link AuthProvider}
   */
  protected AuthProvider getAuthProvider() {
    return authProvider;
  }

  private AuthProviderProxy createAuthProvider(Properties properties) {
    String authProvider = readProperty(AUTH_PROVIDER_PROP, AUTH_PROVIDER_MONGO, false);
    String mapper = readProperty(MongoAuth.PROPERTY_COLLECTION_NAME, null, true);
    if (authProvider.equals(AUTH_PROVIDER_MONGO)) {
      return new AuthProviderProxy(initMongoAuthProvider(mapper), mapper);
    } else {
      throw new UnsupportedOperationException("unsupported authprovider: " + authProvider);
    }
  }

  /**
   * Init the Authentication Service
   */
  private AuthProvider initMongoAuthProvider(String mapper) {
    IDataStore store = getNetRelay().getDatastore();
    if (!(store instanceof MongoDataStore)) {
      throw new IllegalArgumentException("MongoAuthProvider expects a MongoDataStore");
    }
    JsonObject config = new JsonObject();
    String saltStyle = readProperty(MongoAuth.PROPERTY_SALT_STYLE, HashSaltStyle.NO_SALT.toString(), false);
    config.put(MongoAuth.PROPERTY_SALT_STYLE, HashSaltStyle.valueOf(saltStyle));
    MongoAuth auth = MongoAuth.create(((MongoDataStore) store).getMongoClient(), config);

    auth.setPasswordField(readProperty(MongoAuth.PROPERTY_PASSWORD_FIELD, null, true));
    auth.setUsernameField(readProperty(MongoAuth.PROPERTY_USERNAME_FIELD, null, true));
    auth.setCollectionName(mapper);

    String roleField = readProperty(MongoAuth.PROPERTY_ROLE_FIELD, null, false);
    if (roleField != null) {
      auth.setRoleField(roleField);
    }
    String saltField = readProperty(MongoAuth.PROPERTY_SALT_FIELD, null, false);
    if (saltField != null) {
      auth.setSaltField(saltField);
    }

    return auth;
  }

  class AuthProviderProxy implements AuthProvider {
    AuthProvider prov;
    String mapper;

    AuthProviderProxy(AuthProvider prov, String mapper) {
      this.prov = prov;
      this.mapper = mapper;
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.vertx.ext.auth.AuthProvider#authenticate(io.vertx.core.json.JsonObject, io.vertx.core.Handler)
     */
    @Override
    public void authenticate(JsonObject arg0, Handler<AsyncResult<User>> handler) {
      prov.authenticate(arg0, result -> {
        if (result.failed()) {
          LOGGER.info("Authentication failed: " + result.cause());
          handler.handle(result);
        } else {
          User user = result.result();
          user.principal().put(MAPPERNAME_IN_PRINCIPAL, mapper);
          handler.handle(Future.succeededFuture(user));
        }
      });
    }

    /**
     * Get the internal instance of {@link AuthProvider} to access specific configuration infos
     * 
     * @return the internal provider
     */
    public AuthProvider getProvider() {
      return prov;
    }

  }

}

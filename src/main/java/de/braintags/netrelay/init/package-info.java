/*
 * #%L vertx-pojo-mapper-common %% Copyright (C) 2015 Braintags GmbH %% All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html #L%
 */
/**
 * To create and launch an instance of NetRelay, we are creating a Main Verticle like in the example below:
 * 
 * [source, java]
 * ----
 * {@link examples.Main}
 * ----
 * 
 * In this example we are defining first the settings path, which will be used to read or generate the settings, by
 * which NetRelay will be initialized. After we are adding this path to the deployment options and deploy NetRelay with
 * it. If the path is not set, then the settings are searched or generated inside the users directory inside a
 * subdirectory ".netrelay".
 * 
 * ==== Launching NetRelay in eclipse
 * To launch NetRelay directly from eclipse, create a java run configuration and:
 * 
 * * under "project" choose yours
 * * set the Main-class to "io.vertx.core.Starter"
 * * go to the tab "Arguments" and add "run de.braintags.netrelay.fairytale.Main" to the program arguments, where the
 * class should be the classpath to your Main class, of course. +
 * 
 * When you are now launching the application for the first time, it will shown an InitException like that:
 * 
 * ----
 * de.braintags.io.vertx.util.exception.InitException: Settings file did not exist
 *      and was created new in path src/main/resources/fairytale-settings.json.
 *      NOTE: edit the file, set edited to true and restart the server
 *        at de.braintags.netrelay.init.Settings.loadSettings(Settings.java:182)
 *        at de.braintags.netrelay.init.Settings.loadSettings(Settings.java:159)
 *        ...
 * ----
 * 
 * If the settings file does't exist in the expected location, NetRelay will create one with some default adjustments
 * and exit the application. If you would start again, then again an InitException would be thrown, which tells you,
 * that you have to edit the settings. +
 * 
 * ==== Editing the default settings
 * 
 * The minimum you will have to edit to get NetRelay running, is the edited flag and the datastoreSettings. In the upper
 * part of the settings you find the flag "edited", which you will have to set to true. +
 * Additionally you have to edit the datastoreSettings. In this example we are expecting, that at your place a MongoDb
 * is running.
 * 
 * [source, json]
 * ----
 * {
 *   "serverPort" : 8080,
 *   "hostName" : "localhost",
 *   "edited" : true,
 *   "defaultLoginPage" : "/backend/login.html",
 *   "datastoreSettings" : {
 *   "datastoreInit" : "de.braintags.io.vertx.pojomapper.mongo.init.MongoDataStoreInit",
 *   "properties" : {
 *     "startMongoLocal" : "false",
 *     "handleReferencedRecursive" : "true",
 *     "localPort" : "27018",
 *     "connection_string" : "mongodb://localhost:27017",
 *     "shared" : "false"
 *   },
 *   "databaseName" : "fairytale",
 *   ...
 * }
 * ----
 * 
 * If you prefer a MySql, then here is the suitable configuration template:
 * 
 * [source, json]
 * ----
 * 
 *   "datastoreSettings" : {
 *     "datastoreInit" : "de.braintags.io.vertx.pojomapper.mysql.init.MySqlDataStoreinit",
 *     "properties" : {
 *     "host" : "localhost",
 *     "port" : "3306",
 *     "username" : "username",
 *     "password" : "password",
 *     "shared" : "false"
 *   },
 *   "databaseName" : "fairytale"
 * },
 * ...
 * 
 * ----
 * 
 * If you are now launching the application, you should no more get an exception. +
 * To get a first success:
 * 
 * * add a new directory to your project with the name "templates"
 * * Add a new file "index.html" to the templates directory
 * * add the magic text "Hello world" to this template and save it
 * * open "localhost:8080" in a browser
 * 
 * NOTE: When the default settings are generated they contain a definition, which uses Thymeleaf to serve dynamic pages.
 * The adjustment indicates, that the Thymeleaf templates must reside inside a directory named "templates" and that is
 * shall be activated on nearly any route.
 * 
 * 
 * === The structure of the settings
 * 
 * The settings are consisting mainly of five parts:
 * 
 * * some application specific adjustments like the port, where the server shall run, or the even the
 * {@link de.braintags.netrelay.init.MailClientSettings} to enable the system to send and receive emails by controllers
 * or processors
 * * the datastore settings to define, which {@link de.braintags.io.vertx.pojomapper.IDataStore} shall be used by
 * NetRelay. ( This will include the use of the coming MultiDataStores from the project
 * link:https://github.com/BraintagsGmbH/vertx-pojo-mapper[ vertx-pojo-mapper ] )
 * * the Router / Controller definitions to configure {@link de.braintags.netrelay.controller.IController}s and to
 * define, which Controller shall be executed under which route
 * * the processor definitions contain the configuration of regular processed jobs
 * * the mapping definitions contain the specification of the pojo-mappers, which are used by the current application
 * 
 * 
 * 
 */
package de.braintags.netrelay.init;

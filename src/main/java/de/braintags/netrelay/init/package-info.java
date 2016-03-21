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
 * * under "project" choose your project
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
 * The adjustment indicates, that the Thymeleaf templates must reside inside a directory named "templates" and that
 * Thymeleaf
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
 * ==== Controller / Router definitions
 * 
 * The Controller / Router definitions of the settings are determining, which logic is executed by which route.
 * 
 * In general such a definitions consists of:
 * 
 * * name +
 * the name specified here is used for display
 * * controller +
 * the Class as instance of {@link de.braintags.netrelay.controller.IController}, which shall be executed here
 * * active +
 * possibility to deactivate an entry
 * * routes +
 * a comma separated list of routes, where the controller shall be executed, following the syntax defined by
 * {@link io.vertx.ext.web}
 * * httpMethod +
 * if the controller shall be executed under a certain http method ( POST / GET etc. ), then this is defined here.
 * Default is null.
 * * blocking definition +
 * if the Controller shall be executed blocking, then this value is set to true. Default is false.
 * * failureDefinition +
 * set it to true, to define the current entry to be used as failure definition, which is executed, when an error
 * occured. Default is false. ( see the second example bleow )
 * * handlerProperties +
 * a sub object, where controller specific properties are defined ( see the examples below ). The controller specific
 * properties should be contained inside the documentation of the controller
 * * captureCollection *
 * possibility to define captures like known from vertx web. Will be described more exact in the section of the
 * PersistenceController
 * 
 * [source, json]
 * ----
 * 
 * {
 *   "name" : "ThymeleafTemplateController",
 *   "controller" : "de.braintags.netrelay.controller.impl.ThymeleafTemplateController",
 *   "active" : true,
 *   "routes" : [ "/*" ],
 *   "httpMethod" : null,
 *   "blocking" : false,
 *   "failureDefinition" : false,
 *   "handlerProperties" : {
 *     "templateDirectory" : "templates",
 *     "mode" : "XHTML",
 *     "contentType" : "text/html",
 *     "cacheEnabled" : "false"
 *   },
 *   "captureCollection" : null
 * }
 * 
 * ----
 * 
 * The example above displays the configuration of the template controller. The controller class uses Thymeleaf as
 * template engine and is activated on any route. The properties contain the controller specific adjustments, like
 * caching controle, for instance.
 * 
 * 
 * 
 * [source, json]
 * ----
 * 
 * {
 *   "name" : "FailureController",
 *   "controller" : "de.braintags.netrelay.controller.impl.FailureController",
 *   "routes" : null,
 *   "httpMethod" : null,
 *   "blocking" : false,
 *   "failureDefinition" : true,
 *   "handlerProperties" : {
 *     "EX:java.lang.Exception" : "/error/exception.html",
 *     "ERR:404" : "/error/NotFound.html",
 *     "DEFAULT" : "/error/defaultError.html"
 *   },
 *   "captureCollection" : null
 * }
 * 
 * ----
 * The example above displays the specification of a failure controller by using the class
 * {@link de.braintags.netrelay.controller.impl.FailureController}, where resulting errorpages can be defined in
 * dependency to an exception or an error code
 * 
 * 
 * 
 * ==== Processor definitions
 * 
 * Processors are classes, which are executed regular inside a defined time frame. A definitions constists of:
 * 
 * * name +
 * the name specified here is used for display
 * * processorClass +
 * a class as an extension of {@link de.braintags.netrelay.processor.IProcessor}, which is executed
 * * active +
 * possibility to deactivate a processor
 * * timeDef +
 * the definition of the sequence, when the processor is executed. Currently this are milliseconds, other formats will
 * follow
 * * cancelOnError +
 * if set to true and an error occurs, then the processor is stopped
 * * processorProperties +
 * properties to configure the processor. The properties and their existing values must be taken from the documentation
 * of the used processorClass
 * 
 * 
 * [source, json]
 * ----
 * {
 *   "name" : "WelcomeMailProcessor",
 *   "processorClass" : "de.braintags.testproject.processor.WelcomMail",
 *   "active" : true,
 *   "timeDef" : "60000",
 *   "cancelOnError" : false,
 *   "processorProperties" : {
 *     "templateDirectory" : "mailTemplates",
 *     "mode" : "XHTML",
 *     "cacheEnabled" : "false",
 *     "from" : "info@test.de",
 *     "template" : "/mails/welcome.html",
 *     "subject" : "Welcome at test.de",
 *     "inline" : "false",
 *     "host" : "mailer.net",
 *     "port" : "8080"
 *   }
 * }
 * 
 * 
 * ----
 * 
 * The example cofiguration above displays the adjustments for a processor, which checks each 60 seconds for new members
 * in the system and sends them a welcome mail, where the content of the mail is created by a thymeleaf template
 * 
 * 
 * ==== Mapping definitions
 * With this section you are specifying the existing mappers of the system. Each entry consists of a key and the class
 * of the pojo mapper, which shall be connected to this key. The keys, which are defined here, will be used be the
 * PersistenceController to reference onto a mapper.
 * 
 * 
 * [source, json]
 * ----
 * 
 * "mappingDefinitions" : {
 *   "mapperMap" : {
 *     "Member" : "de.braintags.netrelay.model.Member",
 *     "Customer" : "de.braintags.testshop.web.model.Customer",
 *     "ShopCart" : "de.braintags.testshop.web.model.ShopCart"
 *   }
 * }
 * 
 * ----
 * 
 * 
 */
package de.braintags.netrelay.init;

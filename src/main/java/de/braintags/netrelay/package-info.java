/*-
 * #%L
 * netrelay
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
/**
 * :numbered:
 * :toc: left
 * :toclevels: 3
 * 
 * = NetRelay^(R)^ +
 * web application framework with MVC based on vert.x-web
 * 
 * NetRelay is a web application framework based on vert.x and vert.x-web. It is styled to allow a configurable,
 * transparent definition of routings inside a web application by using adjustable, reusable instances, the Controllers.
 * 
 * == The idea
 * * Concentrates the routing definitions in one well defined configuration file rather than spreading them across the
 * projects classes.
 * * Brings reusable entities like Controllers and Processors, so that a new project can be defined rather than
 * programmed
 * 
 * == Quick-Start
 * If you are searching for a very quick entry with a prepared, ready to use project based on NetRelay, you should go to
 * link:https://github.com/BraintagsGmbH/NetRelay-Demoproject[ Quickstart with NetRelay-Demoproject]
 * 
 * == Using NetRelay inside your build environments
 * To use this project, add the following dependency to the _dependencies_ section of your build descriptor:
 * 
 * 
 * * Maven (in your `pom.xml`):
 *
 * [source,xml,subs="+attributes"]
 * ----
 * <dependency>
 *   <groupId>${maven.groupId}</groupId>
 *   <artifactId>${maven.artifactId}</artifactId>
 *   <version>${maven.version}</version>
 * </dependency>
 * ----
 *
 * * Gradle (in your `build.gradle` file):
 *
 * [source,groovy,subs="+attributes"]
 * ----
 * dependencies {
 *   compile '${maven.groupId}:${maven.artifactId}:${maven.version}'
 * }
 * ----
 *
 * == Initializing NetRelay
 * {@link de.braintags.netrelay.init}
 * 
 * {@link de.braintags.netrelay.controller}
 * 
 * {@link de.braintags.netrelay.processor}
 * 
 * == Adding a KeyGenerator Verticle
 * 
 * To be able to use a clean key generator for your mappers, you should launch the
 * {@link de.braintags.vertx.keygenerator.KeyGeneratorVerticle} from the project
 * link:https://github.com/BraintagsGmbH/vertx-key-generator[ vertx-key-generator ].
 * 
 * [source, java]
 * ----
 * private void initKeyGeneratorVerticle(Vertx vertx, Future<Void> startFuture) {
 *   DeploymentOptions options = new DeploymentOptions();
 *   File dir = new File(SETTINGS_LOCATION).getParentFile();
 *   String settingsLocation = new File(dir, "KeyGeneratorSettings.json").getPath();
 *   LOGGER.info("Settings for KeyGenerator: " + settingsLocation);
 *   options.setConfig(new JsonObject().put(KeyGeneratorSettings.SETTINGS_LOCATION_PROPERTY, settingsLocation));
 *   vertx.deployVerticle(KeyGeneratorVerticle.class.getName(), options, result -> {
 *     if (result.failed()) {
 *       startFuture.fail(result.cause());
 *     } else {
 *       LOGGER.info(KeyGeneratorVerticle.class.getSimpleName() + " successfully launched: " + result.result());
 *       startFuture.complete();
 *     }
 *   });
 * }
 * 
 * ----
 * 
 * 
 * == Further links
 *
 * * link:https://github.com/BraintagsGmbH/NetRelay-Controller[NetRelay-Controller] contains several ready to use
 * controllers +
 * 
 * * link:https://github.com/BraintagsGmbH/NetRelay-PdfController[NetRelay-PdfController]: Controllers and processors
 * for NetRelay, which are dealing with pdf. +
 * 
 * * link:https://github.com/BraintagsGmbH/NetRelay-Demoproject[NetRelay-Demoproject]: a demo project using NetRelay +
 * 
 *
 *
 */
@Document(fileName = "index.adoc")
package de.braintags.netrelay;

import io.vertx.docgen.Document;

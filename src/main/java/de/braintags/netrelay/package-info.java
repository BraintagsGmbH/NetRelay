/**
 * :numbered:
 * :toc: left
 * :toclevels: 3
 * 
 * = NetRelay^(R)^
 * 
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
 * * Maven (in your `pom.xml`):
 *
 * [source,xml,subs="+attributes"]
 * ----
 * <dependency>
 * <groupId>de.braintags</groupId>
 * <artifactId>{maven.artifactId}</artifactId>
 * <version>{maven.version}</version>
 * </dependency>
 * ----
 *
 * * Gradle (in your `build.gradle` file):
 *
 * [source,groovy,subs="+attributes"]
 * ----
 * compile "de.braintags:{maven.artifactId}:{maven.version}"
 * ----
 *
 * 
 * === Initialize by Settings
 * {@link de.braintags.netrelay.init}
 * 
 * 
 * 
 * == Further links
 *
 *
 */
@Document(fileName = "index.adoc")
package de.braintags.netrelay;

import io.vertx.docgen.Document;

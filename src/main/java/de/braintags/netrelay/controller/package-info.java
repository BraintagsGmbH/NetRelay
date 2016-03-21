/*
 * #%L vertx-pojo-mapper-common %% Copyright (C) 2015 Braintags GmbH %% All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html #L%
 */
/**
 * == Controller
 * 
 * As explained before, a controller is a handler, which shall be executed for a certain route definition and is
 * configured as part of the settings. NetRelay contains already several ready to use controllers, which can be simply
 * added and configured inside the settings. The documentation of the possible parameters and properties can be found in
 * the javadoc of the appropriate classes.
 * 
 * === Creating a new Controller
 * Adding a new controller is simply done by implementing {@link de.braintags.netrelay.controller.IController} or by
 * extending {@link de.braintags.netrelay.controller.impl.AbstractController}, for instance. +
 * In our example here we will create a new controller, which will add the test "Hello world" into the context, from
 * where it can be read from out of a template. The name of the variable in the context must set inside the
 * configuration in the settings. +
 * 
 * [source, java]
 * ----
 * {@link examples.HelloWorldController}
 * 
 * ----
 * 
 * In the example code you can see, that the name, by which the text is stored inside the context, is read from the
 * properties. The corresponding configuration part from the settings looks like that:
 * 
 * [source, json]
 * ----
 * {
 *   "name" : "HelloWorld",
 *   "active" : true,
 *   "routes" : [ "/helloWorld.html"],
 *   "blocking" : false,
 *   "failureDefinition" : false,
 *   "controller" : "de.braintags.netrelay.fairytale.controller.HelloWorldController",
 *   "httpMethod" : null,
 *   "handlerProperties" : {
 *     "helloProperty" : "myProperty"
 *   },
 *   "captureCollection" : null
 * }
 * 
 * ----
 * 
 * and the snippet from the html template, which we created inside the template directory with the name
 * "helloWorld.html", looks like that:
 * 
 * 
 * [source, html]
 * ----
 * 
 * <!DOCTYPE html SYSTEM "http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-4.dtd">
 * <html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
 * <head>
 * </head>
 * <body>
 * <p th:text="${context.get( 'myProperty') }">
 * </p>
 * </body>
 * </html>
 * 
 * ----
 * 
 * 
 * === Existing Controllers
 * 
 * === Capture Collection
 * 
 */
package de.braintags.netrelay.controller;

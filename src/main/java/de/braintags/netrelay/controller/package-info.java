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
 * == Controller
 * 
 * As explained before, a controller is a handler, which shall be executed for a certain route definition and is
 * configured as part of the settings. NetRelay and the project NetRelay-Controllers, contain already several ready to
 * use controllers, which can be simply added and configured inside the settings. The documentation of the possible
 * parameters and properties can be found in the javadoc of the appropriate classes.
 * 
 * === Creating a new Controller
 * Adding a new controller is simply done by implementing {@link de.braintags.netrelay.controller.IController} or by
 * extending {@link de.braintags.netrelay.controller.AbstractController}, for instance. +
 * In our example here we will create a new controller, which will add the text "Hello world" into the context, from
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
 * Inside the core project of NetRelay are existing only a few, fundamental controllers. Further, more complex
 * controllers can be found in the project link:https://github.com/BraintagsGmbH/NetRelay-Controller[
 * NetRelay-Controller]. The configuration options of each Controller are described inside the javadoc.
 * 
 * * {@link de.braintags.netrelay.controller.BodyController} +
 * A Controller, which creates and uses the Bodyhandler, to read the request body and uploads.
 * The BodyController creates some variables and stores them inside the context, so that they can be used from out of a
 * template, for instance.<br/>
 * 
 * * {@link de.braintags.netrelay.controller.CookieController} +
 * The Cookie-Controller uses teh CookieHandler to decode cookies from the request, makes them
 * available in the RoutingContext and writes them back in the response.
 * 
 * * {@link de.braintags.netrelay.controller.SessionController} +
 * SessionController uses a SessionHandler internally to implement session handling for
 * all browser sessions.
 * 
 * * {@link de.braintags.netrelay.controller.FailureController} +
 * A Controller for failing calls. The Controller can be configured to produce output depending on an error code or an
 * exception. For each of them can be defined a redirect address. If no definition is found, then an internal default
 * output is generated.
 * 
 * * {@link de.braintags.netrelay.controller.StaticController} +
 * A controller to define serving of static contents.
 * 
 * * {@link de.braintags.netrelay.controller.TimeoutController} +
 * This controller defines for the specified routes, after how long time the request processing is stopped.
 * 
 * 
 */
package de.braintags.netrelay.controller;

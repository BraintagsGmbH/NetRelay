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
package de.braintags.netrelay.controller;

/**
 * An action, which can be performed by a request
 * 
 * 
 * @author Michael Remme
 *
 */
public enum Action {
  INSERT(),
  UPDATE(),
  DISPLAY(),
  DELETE();

}
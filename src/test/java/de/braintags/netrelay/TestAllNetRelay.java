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
package de.braintags.netrelay;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ TRequestUtil.class, TestSettings.class, TTemplateController.class, TCaptureParameters.class })
public class TestAllNetRelay {
  // -DBlockedThreadCheckInterval=10000000 -DWarningExceptionTime=10000000 -DtestTimeout=5
  // -Djava.util.logging.config.file=src/main/resources/logging.properties

}

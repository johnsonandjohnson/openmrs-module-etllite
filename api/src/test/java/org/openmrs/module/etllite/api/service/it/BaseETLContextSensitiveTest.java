/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.service.it;

import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Properties;

public abstract class BaseETLContextSensitiveTest extends BaseModuleContextSensitiveTest {
  @Override
  public Properties getRuntimeProperties() {
    // Setup Password for unit-test H2 in-memory DB.
    // We need any password, because ETL config requires password to not be blank
    Properties props = super.getRuntimeProperties();
    props.setProperty("hibernate.connection.password", "Password");
    props.setProperty("connection.password", "Password");
    return props;
  }
}

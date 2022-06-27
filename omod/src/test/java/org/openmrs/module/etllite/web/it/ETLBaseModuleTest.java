/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.web.it;

import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.util.Properties;

public abstract class ETLBaseModuleTest extends BaseModuleWebContextSensitiveTest {

    @Override
    public Properties getRuntimeProperties() {
        Properties props = super.getRuntimeProperties();
        props.setProperty("hibernate.connection.password", "Password");
        props.setProperty("connection.password", "Password");
        return props;
    }
}

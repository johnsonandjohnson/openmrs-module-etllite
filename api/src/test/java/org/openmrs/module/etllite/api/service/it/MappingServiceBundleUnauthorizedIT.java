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

import org.junit.Test;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.module.etllite.api.dao.MappingDao;
import org.openmrs.module.etllite.api.service.MappingService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MappingService Integration Test Class
 */
public class MappingServiceBundleUnauthorizedIT extends BaseModuleContextSensitiveTest {

    @Autowired
    private MappingService mappingService;

    @Autowired
    private MappingDao mappingDao;

    @Override
    public void authenticate() {
        // disable default authentication
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldForbidUnauthorizedUserToFind() {
        mappingService.findAll();
    }
}

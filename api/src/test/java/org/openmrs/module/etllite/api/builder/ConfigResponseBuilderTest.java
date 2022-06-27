/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.builder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.etllite.api.BaseTest;
import org.openmrs.module.etllite.api.ETLTestHelper;
import org.openmrs.module.etllite.api.contract.ConfigResponse;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * ConfigResponseBuilder Test Class
 *
 * @author nanakapa
 */

@RunWith(MockitoJUnitRunner.class)
public class ConfigResponseBuilderTest extends BaseTest {

    private ConfigResponseBuilder configResponseBuilder = new ConfigResponseBuilder();

    @Test
    public void shouldBuildResponseConfigFromConfig() {
        // Given and When
        ConfigResponse configResponse = configResponseBuilder.createFrom(ETLTestHelper.setUpETLDatabase1());

        // Then
        assertNotNull(configResponse);
        assertThat(configResponse.getName(), equalTo(ETLTestHelper.ETL_DB_NAME));
        assertThat(configResponse.getType(), equalTo(ETLTestHelper.ETL_DB_TYPE));
        assertThat(configResponse.getUrl(), equalTo(ETLTestHelper.ETL_DB_URL));
        assertThat(configResponse.getUser(), equalTo(ETLTestHelper.ETL_DB_USER));
        assertThat(configResponse.getQuery(), equalTo(ETLTestHelper.ETL_TEST_QUERY));
    }
}

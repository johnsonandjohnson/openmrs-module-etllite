/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.etllite.api.BaseTest;
import org.openmrs.module.etllite.api.ETLTestHelper;
import org.openmrs.module.etllite.api.builder.ConfigBuilder;
import org.openmrs.module.etllite.api.domain.Config;
import org.openmrs.module.etllite.api.util.EncryptionUtil;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * ConfigBuilder Test Class
 *
 * @author nanakapa
 */

@RunWith(MockitoJUnitRunner.class)
public class ConfigBuilderTest extends BaseTest {

    private static final String ENCRYPTED_DB_PASSWORD = "$#^%$!%$&!";

    @InjectMocks
    private ConfigBuilder configBuilder = new ConfigBuilder();

    @Mock
    private EncryptionUtil encryptionUtil;

    @Test
    public void shouldBuildConfigFromConfigRequest() {

        //Given
        given(encryptionUtil.encryptAsString(ETLTestHelper.ETL_DB_PWD)).willReturn(ENCRYPTED_DB_PASSWORD);

        // When
        Config config = configBuilder.createConfig(ETLTestHelper.setUpETLDatabase1(), new Config());

        // Then
        assertNotNull(config);
        assertThat(config.getName(), equalTo(ETLTestHelper.ETL_DB_NAME));
        assertThat(config.getType(), equalTo(ETLTestHelper.ETL_DB_TYPE));
        assertThat(config.getUrl(), equalTo(ETLTestHelper.ETL_DB_URL));
        assertThat(config.getUser(), equalTo(ETLTestHelper.ETL_DB_USER));
        assertThat(config.getDbPassword(), equalTo(ENCRYPTED_DB_PASSWORD));
        assertThat(config.getQuery(), equalTo(ETLTestHelper.ETL_TEST_QUERY));
    }

    @Test
    public void shouldDecryptPasswordIfThePasswordToBeDecryptedIsNotNullOrEmpty() {
        //Given
        given(encryptionUtil.decryptAsString(ENCRYPTED_DB_PASSWORD)).willReturn(ETLTestHelper.ETL_DB_PWD);

        String decryptedPassword = configBuilder.decryptPassword(ENCRYPTED_DB_PASSWORD);
        assertNotNull(decryptedPassword);
        assertThat(decryptedPassword, equalTo(ETLTestHelper.ETL_DB_PWD));
        verify(encryptionUtil, times(1)).decryptAsString(ENCRYPTED_DB_PASSWORD);
    }

    @Test
    public void shouldReturnEmptyStringIfThePasswordToBeDecryptedIsNullOrEmpty() {
        String decryptedPassword = configBuilder.decryptPassword("");
        assertTrue(decryptedPassword.isEmpty());
    }
}

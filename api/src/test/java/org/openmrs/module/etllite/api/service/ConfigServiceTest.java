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

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.etllite.api.BaseTest;
import org.openmrs.module.etllite.api.ETLTestHelper;
import org.openmrs.module.etllite.api.builder.ConfigBuilder;
import org.openmrs.module.etllite.api.constants.Constants;
import org.openmrs.module.etllite.api.contract.ConfigRequestWrapper;
import org.openmrs.module.etllite.api.domain.Config;
import org.openmrs.module.etllite.api.service.impl.ConfigServiceImpl;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * ConfigService Test Class
 *
 * @author nanakapa
 */

@RunWith(MockitoJUnitRunner.class)
public class ConfigServiceTest extends BaseTest {

    private static final int EXPECTED_DEFAULT_CONFIG_SIZE = 2;

    // Validation is skipped in this place because checkstyle 2.10 VisibilityModifier
    // doesn't support public @Rule annotation
    //CHECKSTYLE:OFF
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    //CHECKSTYLE:ON

    private ConfigRequestWrapper configRequestWrapper;

    private Config database1;

    private Config database2;

    @Mock
    private SettingsManagerService settingsManagerService;

    @Mock
    private ConfigBuilder configBuilder;

    @Mock
    private IOUtils ioUtils;

    @InjectMocks
    private ConfigServiceImpl configService = new ConfigServiceImpl();

    @Mock
    private DriverManagerDataSource basicDataSource;

    private Map<String, Config> configs = new HashMap<>();

    @Before
    public void setUp() throws IOException, SQLException {
        // Setup two ETL databases
        database1 = ETLTestHelper.setUpETLDatabase1();
        database2 = ETLTestHelper.setUpETLDatabase2();
        configs.put(ETLTestHelper.ETL_DB_NAME, database1);
        configs.put(ETLTestHelper.ETL_DB_NAME2, database2);

        configRequestWrapper = ETLTestHelper.setUpETLConfigRequest();
        String json = json(configRequestWrapper);
        mockGetRawConfig(json);

        given(configBuilder.decryptPassword(ETLTestHelper.ETL_DB_PWD)).willReturn(ETLTestHelper.ETL_DB_PWD);
        configService.initialize();
    }

    @Test
    public void shouldReturnDatabaseSettingsForTheSpecifiedETLDatabase() {
        Config database = configService.getConfig(ETLTestHelper.ETL_DB_NAME);
        assertNotNull(database);
        assertThat(database.getName(), is(ETLTestHelper.ETL_DB_NAME));
        assertThat(database.getType(), is(ETLTestHelper.ETL_DB_TYPE));
        assertThat(database.getUrl(), is(ETLTestHelper.ETL_DB_URL));
        assertThat(database.getQuery(), is(ETLTestHelper.ETL_TEST_QUERY));
        assertThat(database.getUser(), is(ETLTestHelper.ETL_DB_USER));
    }

    @Test
    public void shouldReturnTrueIfTheSpecifiedETLDatabaseIsPresent() {
        boolean configExists = configService.hasConfig(ETLTestHelper.ETL_DB_NAME);
        assertTrue(configExists);
    }

    @Test
    public void shouldThrowIllegalArgumentIfWeTryToRetrieveMissingETLDatabase() {
        expectedException.expect(IllegalArgumentException.class);
        configService.getConfig(ETLTestHelper.NON_EXISTING_DB);
    }

    @Test
    public void shouldReturnFalseIfWeCheckForExistenceOfAMissingETLDatabase() {
        boolean configExists = configService.hasConfig(ETLTestHelper.NON_EXISTING_DB);
        assertFalse(configExists);
    }

    @Test
    public void shouldReturnAllETLDatabaseSettings() {
        List<Config> configList = configService.allConfigs();
        assertThat(configList.size(), is(2));

        assertThat(configList.get(0).getName(), is(ETLTestHelper.ETL_DB_NAME));
        assertThat(configList.get(0).getType(), is(ETLTestHelper.ETL_DB_TYPE));
        assertThat(configList.get(0).getUrl(), is(ETLTestHelper.ETL_DB_URL));
        assertThat(configList.get(0).getQuery(), is(ETLTestHelper.ETL_TEST_QUERY));
        assertThat(configList.get(0).getUser(), is(ETLTestHelper.ETL_DB_USER));

        assertThat(configList.get(1).getName(), is(ETLTestHelper.ETL_DB_NAME2));
        assertThat(configList.get(1).getType(), is(ETLTestHelper.ETL_DB_TYPE));
        assertThat(configList.get(1).getUrl(), is(ETLTestHelper.ETL_DB_URL));
        assertThat(configList.get(1).getQuery(), is(ETLTestHelper.ETL_TEST_QUERY));
        assertThat(configList.get(1).getUser(), is(ETLTestHelper.ETL_DB_USER));
    }

    @Test
    public void shouldDeleteTheSpecifiedETLDatabaseSettings() {
        assertThat(configService.allConfigs().size(), is(2));
        configService.deleteConfig(ETLTestHelper.ETL_DB_NAME);
        assertFalse(configService.hasConfig(ETLTestHelper.ETL_DB_NAME));
        assertThat(configService.allConfigs().size(), is(1));
        assertTrue(configService.hasConfig(ETLTestHelper.ETL_DB_NAME2));
    }

    @Test
    public void shouldDeleteAllETLDatabaseSettings() {
        assertThat(configService.allConfigs().size(), is(2));
        configService.deleteAll();
        assertTrue(configService.allConfigs().isEmpty());
    }

    @Test
    public void shouldReturnServicesWhenGetServicesIsCalled() {
        String services = configService.getServices();
        assertNotNull(services);
        assertThat(services, equalTo(ETLTestHelper.SERVICES));
    }

    @Test
    public void shouldUpdateDatabaseSettings() throws IOException {
        Config existingDatabase1 = configService.getConfig(ETLTestHelper.ETL_DB_NAME);
        Config existingDatabase2 = configService.getConfig(ETLTestHelper.ETL_DB_NAME2);
        database1.setUser("root1");
        configRequestWrapper.setDatabases(new ArrayList<>(Arrays.asList(database1, database2)));
        configRequestWrapper.setServices(ETLTestHelper.SERVICES);
        given(configBuilder.createConfig(database1, existingDatabase1)).willReturn(database1);
        given(configBuilder.createConfig(database2, existingDatabase2)).willReturn(database2);

        String json = json(configRequestWrapper);
        mockGetRawConfig(json);

        configService.createOrUpdateConfigs(configRequestWrapper);
        assertThat(configService.allConfigs().size(), is(2));
        assertThat(configService.getConfig(ETLTestHelper.ETL_DB_NAME).getUser(), equalTo("root1"));
        verify(settingsManagerService, times(2)).getRawConfig(ETLTestHelper.CONFIG_FILE_NAME);
        verify(settingsManagerService, times(1)).saveRawConfig(ETLTestHelper.CONFIG_FILE_NAME, new ByteArrayResource(json
                .getBytes()));
    }

    @Test
    public void shouldCreateDatabaseSettings() throws IOException {
        Config existingDatabase1 = configService.getConfig(ETLTestHelper.ETL_DB_NAME);
        Config existingDatabase2 = configService.getConfig(ETLTestHelper.ETL_DB_NAME2);

        Config newDatabase = ETLTestHelper.setUpETLDatabase3();

        configRequestWrapper.setDatabases(new ArrayList<>(Arrays.asList(database1, database2, newDatabase)));
        configRequestWrapper.setServices(ETLTestHelper.SERVICES);
        given(configBuilder.createConfig(database1, existingDatabase1)).willReturn(database1);
        given(configBuilder.createConfig(database2, existingDatabase2)).willReturn(database2);
        given(configBuilder.createConfig(newDatabase, new Config())).willReturn(newDatabase);

        String json = json(configRequestWrapper);
        mockGetRawConfig(json);

        configService.createOrUpdateConfigs(configRequestWrapper);
        assertThat(configService.allConfigs().size(), is(3));
        assertThat(configService.getConfig(ETLTestHelper.ETL_DB_NAME3).getName(), equalTo(ETLTestHelper.ETL_DB_NAME3));
        verify(settingsManagerService, times(2)).getRawConfig(ETLTestHelper.CONFIG_FILE_NAME);
    }

    @Test
    public void shouldNotUpdateDatabaseSettingsDueToInvalidEnumType() throws IOException {
        Config configWithInvalidEnumType = ETLTestHelper.setUpConfigWithInvalidEnumTyp();
        mockCreateConfig(configWithInvalidEnumType);
        String json = json(configRequestWrapper);
        mockGetRawConfig(json);

        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage(Constants.INVALID_ENUM_TYPE_MESSAGE);
        configService.createOrUpdateConfigs(configRequestWrapper);
    }

    @Test
    public void shouldNotCreateDatabaseSettingsDueToMissingPassword() throws IOException {
        Config configWithBlankPassword = ETLTestHelper.setUpConfigWithBlankPassword();
        mockCreateConfig(configWithBlankPassword);
        String json = json(configRequestWrapper);
        mockGetRawConfig(json);

        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage(Constants.INVALID_CONFIG_DB_PASSWORD_MESSAGE);
        configService.createOrUpdateConfigs(configRequestWrapper);
    }

    @Test
    public void shouldNotUpdateDatabaseSettingsDueToNullInConfig() throws IOException {
        Config configWithAllAttributesNull = ETLTestHelper.setUpConfigWithAllAttributesNull();
        mockCreateConfig(configWithAllAttributesNull);
        String json = json(configRequestWrapper);
        mockGetRawConfig(json);

        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage(Constants.INVALID_CONFIG_TYPE_MESSAGE);
        exceptionRule.expectMessage(Constants.INVALID_CONFIG_URL_MESSAGE);
        exceptionRule.expectMessage(Constants.INVALID_CONFIG_USER_MESSAGE);
        exceptionRule.expectMessage(Constants.INVALID_CONFIG_DB_PASSWORD_MESSAGE);
        exceptionRule.expectMessage(Constants.INVALID_CONFIG_NAME_MESSAGE);
        configService.createOrUpdateConfigs(configRequestWrapper);
    }

    @Test
    public void shouldNotUpdateDatabaseSettingsDueToBlankInConfig() throws IOException {
        Config configWithAllAttributesBlank = ETLTestHelper.setUpConfigWithAllAttributesBlank();
        mockCreateConfig(configWithAllAttributesBlank);
        String json = json(configRequestWrapper);
        mockGetRawConfig(json);

        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage(Constants.INVALID_ENUM_TYPE_MESSAGE);
        exceptionRule.expectMessage(Constants.INVALID_CONFIG_TYPE_MESSAGE);
        exceptionRule.expectMessage(Constants.INVALID_CONFIG_URL_MESSAGE);
        exceptionRule.expectMessage(Constants.INVALID_CONFIG_USER_MESSAGE);
        exceptionRule.expectMessage(Constants.INVALID_CONFIG_DB_PASSWORD_MESSAGE);
        exceptionRule.expectMessage(Constants.INVALID_CONFIG_NAME_MESSAGE);
        configService.createOrUpdateConfigs(configRequestWrapper);
    }

    @Test
    public void shouldInitializeDatabaseSettingsEvenIfGetRawConfigThrowException() {
        given(settingsManagerService.getRawConfig(ETLTestHelper.CONFIG_FILE_NAME)).willThrow(new IllegalStateException());
        configService.initialize();
        assertThat(configService.allConfigs().size(), is(EXPECTED_DEFAULT_CONFIG_SIZE));
    }

    private void mockGetRawConfig(String json) throws IOException {
        try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
            given(settingsManagerService.getRawConfig(ETLTestHelper.CONFIG_FILE_NAME)).willReturn(is);
        }
    }

    private void mockCreateConfig(Config config) {
        Config existingDatabase1 = configService.getConfig(ETLTestHelper.ETL_DB_NAME);
        configRequestWrapper.setDatabases(new ArrayList<>(Collections.singletonList(config)));
        configRequestWrapper.setServices(ETLTestHelper.SERVICES);
        given(configBuilder.createConfig(config, existingDatabase1)).willReturn(config);
    }
}

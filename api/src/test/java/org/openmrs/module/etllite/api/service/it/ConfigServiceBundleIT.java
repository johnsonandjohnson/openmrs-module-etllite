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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.etllite.api.ETLTestHelper;
import org.openmrs.module.etllite.api.contract.ConfigRequestWrapper;
import org.openmrs.module.etllite.api.domain.Config;
import org.openmrs.module.etllite.api.service.ConfigService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * ConfigService Integration Test
 *
 * @author nanakapa
 */
public class ConfigServiceBundleIT extends BaseModuleContextSensitiveTest {

    private Config config;

    @Autowired
    private ConfigService configService;

    @Before
    public void setUp() {
        setUpDatabase();
    }

    @Test
    public void shouldReturnTrueIfDatabaseConnectionIsSuccessful() throws SQLException {
        createETLDatabase();
        assertTrue(configService.testDatabase(ETLTestHelper.ETL_DB_NAME));
    }

    @Test
    public void shouldReturnFalseIfDatabaseConnectionIsNotSuccessful() throws SQLException {
        createETLDatabaseWithInvalidDetails();
        assertFalse(configService.testDatabase(ETLTestHelper.ETL_DB_NAME));
    }

    @Test
    public void shouldReturnAllETLDatabaseSettings() {
        createETLDatabase();
        List<Config> configList = configService.allConfigs();
        assertThat(configList.size(), is(1));
        checkAsserts();
    }

    @Test
    public void shouldReturnTrueIfTheSpecifiedETLDatabaseIsPresent() {
        createETLDatabase();
        boolean configExists = configService.hasConfig(ETLTestHelper.ETL_DB_NAME);
        assertTrue(configExists);
    }

    @Test
    public void shouldReturnDatabaseSettingsForTheSpecifiedETLDatabase() {
        createETLDatabase();
        Config dbConfig = configService.getConfig(ETLTestHelper.ETL_DB_NAME);
        assertNotNull(dbConfig);
        checkAsserts();
    }

    @Test
    public void shouldReturnDataSourceForASpecifiedETLDatabase() {
        createETLDatabase();
        DataSource ds = configService.getDataSource(ETLTestHelper.ETL_DB_NAME);
        DriverManagerDataSource dataSource = (DriverManagerDataSource) ds;
        assertThat(dataSource.getUsername(), is(ETLTestHelper.H2_TEST_DB_USER));
        assertThat(dataSource.getPassword(), is(ETLTestHelper.H2_TEST_DB_PASSWORD));
        assertThat(dataSource.getUrl(), is(ETLTestHelper.H2_TEST_DB_URL));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfWeTryToRetrieveMissingETLDatabase() {
        configService.getConfig(ETLTestHelper.NON_EXISTING_DB);
    }

    @Test
    public void shouldReturnFalseIfWeCheckForExistenceOfAMissingETLDatabase() {
        boolean configExists = configService.hasConfig(ETLTestHelper.NON_EXISTING_DB);
        assertFalse(configExists);
    }

    @Test
    public void shouldDeleteTheSpecifiedETLDatabaseSettings() {
        createETLDatabase();
        assertThat(configService.allConfigs().size(), is(1));
        configService.deleteConfig(ETLTestHelper.ETL_DB_NAME);
        assertFalse(configService.hasConfig(ETLTestHelper.ETL_DB_NAME));
        assertThat(configService.allConfigs().size(), is(0));
    }

    @Test
    public void shouldDeleteAllETLDatabaseSettings() {
        createETLDatabase();
        assertThat(configService.allConfigs().size(), is(1));
        configService.deleteAll();
        assertTrue(configService.allConfigs().isEmpty());
    }

    @Test
    public void shouldReturnServicesWhenGetServicesIsCalled() {
        createETLDatabase();
        String services = configService.getServices();
        assertNotNull(services);
        assertThat(services, equalTo(ETLTestHelper.SERVICES));
    }

    @Test
    public void shouldCreateDatabaseSettings() throws IOException {
        createETLDatabase();
        Config dbConfig = configService.getConfig(ETLTestHelper.ETL_DB_NAME);
        assertNotNull(dbConfig);
        checkAsserts();
    }

    @Test
    public void shouldUpdateDatabaseSettings() throws IOException {
        createETLDatabase();
        Config dbConfig = configService.getConfig(ETLTestHelper.ETL_DB_NAME);
        assertNotNull(dbConfig);
        dbConfig.setQuery("select 1;");

        List<Config> configs = new ArrayList<>();
        configs.add(dbConfig);

        ConfigRequestWrapper configRequestWrapper = new ConfigRequestWrapper();
        configRequestWrapper.setDatabases(configs);
        configRequestWrapper.setServices(ETLTestHelper.SERVICES);
        configService.createOrUpdateConfigs(configRequestWrapper);

        Config updatedConfig = configService.getConfig(ETLTestHelper.ETL_DB_NAME);
        assertNotNull(updatedConfig);
        assertThat(updatedConfig.getQuery(), equalTo("select 1;"));
    }

    @After
    public void tearDown() {
        configService.deleteAll();
    }

    private void setUpDatabase() {
        config = new Config();
        config.setName(ETLTestHelper.ETL_DB_NAME);
        config.setType(ETLTestHelper.ETL_DB_TYPE);
        config.setQuery(ETLTestHelper.ETL_TEST_QUERY);
        config.setUser(ETLTestHelper.H2_TEST_DB_USER);
        config.setDbPassword(ETLTestHelper.H2_TEST_DB_PASSWORD);
        config.setUrl(ETLTestHelper.H2_TEST_DB_URL);
    }

    private void createETLDatabase() {
        List<Config> configs = new ArrayList<>();
        configs.add(config);
        ConfigRequestWrapper configRequestWrapper = new ConfigRequestWrapper();
        configRequestWrapper.setDatabases(configs);
        configRequestWrapper.setServices(ETLTestHelper.SERVICES);
        configService.createOrUpdateConfigs(configRequestWrapper);
    }

    private void createETLDatabaseWithInvalidDetails() {
        List<Config> configs = new ArrayList<>();
        config.setUser("wronguser");
        configs.add(config);
        ConfigRequestWrapper configRequestWrapper = new ConfigRequestWrapper();
        configRequestWrapper.setDatabases(configs);
        configRequestWrapper.setServices(ETLTestHelper.SERVICES);
        configService.createOrUpdateConfigs(configRequestWrapper);
    }

    private void checkAsserts() {
        assertThat(config.getName(), is(ETLTestHelper.ETL_DB_NAME));
        assertThat(config.getType(), is(ETLTestHelper.ETL_DB_TYPE));
        assertThat(config.getUrl(), is(ETLTestHelper.H2_TEST_DB_URL));
        assertThat(config.getQuery(), is(ETLTestHelper.ETL_TEST_QUERY));
        assertThat(config.getUser(), is(ETLTestHelper.H2_TEST_DB_USER));
    }

}

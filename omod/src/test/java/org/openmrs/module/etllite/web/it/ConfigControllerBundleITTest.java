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

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.etllite.api.ETLTestHelper;
import org.openmrs.module.etllite.api.contract.ConfigRequestWrapper;
import org.openmrs.module.etllite.api.domain.Config;
import org.openmrs.module.etllite.api.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ConfigControllerBundleIT Integration Test Class
 *
 * @author nanakapa
 */
@WebAppConfiguration
public class ConfigControllerBundleITTest extends ETLBaseModuleTest {

    private static final String CONFIGS_URL = "/etllite/configs/";

    private MockMvc mockMvc;

    private Config config;

    private ConfigRequestWrapper configRequestWrapper;

    @Autowired
    private ConfigService configService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        setUpDatabase();
    }

    @Test
    public void shouldReturnStatusOkOnSuccessfulDatabaseConnection() throws Exception {
        //Given
        createETLDatabase();

        mockMvc.perform(get(CONFIGS_URL + ETLTestHelper.ETL_DB_NAME + "/test"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType((ETLTestHelper.APPLICATION_JSON_UTF8)))
                .andExpect(content().string("true"));
    }

    @Test
    public void shouldReturnStatusOkWithResponseFalseOnFailedDatabaseConnection() throws Exception {
        //Given
        createETLDatabaseWithInvalidDetails();

        mockMvc.perform(get(CONFIGS_URL + ETLTestHelper.ETL_DB_NAME + "/test"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType((ETLTestHelper.APPLICATION_JSON_UTF8)))
                .andExpect(content().string("false"));
    }

    @Test
    public void shouldReturnStatusOkOnGetAllConfigsWhenNoETLDatabasesExists() throws Exception {
        mockMvc.perform(get(CONFIGS_URL))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType((ETLTestHelper.APPLICATION_JSON_UTF8)));
    }

    @Test
    public void shouldReturnStatusOkOnGetAllConfigsWhenETLDatabasesExists() throws Exception {
        //Given
        createETLDatabase();
        //When and Then
        mockMvc.perform(get(CONFIGS_URL))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType((ETLTestHelper.APPLICATION_JSON_UTF8)));
    }

    @Test
    public void shouldReturnStatusOkOnCreateConfig() throws Exception {
        //Given
        setUpConfigRequest();

        mockMvc.perform(post(CONFIGS_URL).contentType(MediaType.parseMediaType(ETLTestHelper.APPLICATION_JSON_UTF8))
                .content(json(configRequestWrapper).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void shouldReturnStatusOkOnDeleteASpecifiedConfig() throws Exception {
        //Given
        createETLDatabase();

        mockMvc.perform(delete(CONFIGS_URL + "{name}", ETLTestHelper.ETL_DB_NAME))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void shouldReturnStatusOkOnDeleteAllConfigs() throws Exception {
        ///Given
        createETLDatabase();

        //When and Then
        mockMvc.perform(delete(CONFIGS_URL)).andExpect(status().is(HttpStatus.OK.value()));
    }

    @After
    public void cleanDataBase() {
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

    private void setUpConfigRequest() {
        List<Config> configs = new ArrayList<>();
        configs.add(config);
        configRequestWrapper = new ConfigRequestWrapper();
        configRequestWrapper.setDatabases(configs);
        configRequestWrapper.setServices(ETLTestHelper.SERVICES);
    }

    private void createETLDatabase() {
        setUpConfigRequest();
        configService.createOrUpdateConfigs(configRequestWrapper);
    }

    private void createETLDatabaseWithInvalidDetails() {
        config.setUser("wronguser");
        setUpConfigRequest();
        configService.createOrUpdateConfigs(configRequestWrapper);
    }

    private String json(Object obj) throws IOException {
        return new ObjectMapper().writeValueAsString(obj);
    }
}

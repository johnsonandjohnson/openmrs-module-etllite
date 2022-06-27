/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.etllite.api.BaseTest;
import org.openmrs.module.etllite.api.ETLTestHelper;
import org.openmrs.module.etllite.api.builder.ConfigResponseBuilder;
import org.openmrs.module.etllite.api.contract.ConfigRequestWrapper;
import org.openmrs.module.etllite.api.contract.ConfigResponseWrapper;
import org.openmrs.module.etllite.api.domain.Config;
import org.openmrs.module.etllite.api.service.ConfigService;
import org.openmrs.module.etllite.web.controller.ConfigController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ConfigController Test Class
 *
 * @author nanakapa
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigControllerTest extends BaseTest {

    private static final String CONFIGS_URL = "/etllite/configs/";

    private MockMvc mockMvc;

    private Config config1;
    private ConfigRequestWrapper configRequestWrapper;
    private ConfigResponseWrapper configResponseWrapper;

    private List<Config> configList = new ArrayList<>();

    @Mock
    private ConfigService configService;

    @Mock
    private ConfigResponseBuilder configResponseBuilder;

    @InjectMocks
    private ConfigController configController = new ConfigController();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(configController).build();

        config1 = ETLTestHelper.setUpETLDatabase1();
        configList.add(config1);

        configRequestWrapper = ETLTestHelper.setUpETLConfigRequest();
        configResponseWrapper = ETLTestHelper.setUpETLConfigResponse();
    }

    @Test
    public void shouldReturnTrueIfDatabaseConnectionIsSuccessful() throws Exception {
        //Given
        given(configService.testDatabase(ETLTestHelper.ETL_DB_NAME)).willReturn(true);

        //When
        mockMvc.perform(get(CONFIGS_URL + ETLTestHelper.ETL_DB_NAME + "/test"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType((ETLTestHelper.APPLICATION_JSON_UTF8)))
                .andExpect(content().string("true"));
        //Then
        verify(configService, times(1)).testDatabase(ETLTestHelper.ETL_DB_NAME);
    }

    @Test
    public void shouldReturnFalseIfDatabaseConnectionIsUnSuccessful() throws Exception {
        //Given
        given(configService.testDatabase(ETLTestHelper.ETL_DB_NAME)).willReturn(false);

        //When
        mockMvc.perform(get(CONFIGS_URL + ETLTestHelper.ETL_DB_NAME + "/test"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(ETLTestHelper.APPLICATION_JSON_UTF8))
                .andExpect(content().string("false"));
        //Then
        verify(configService, times(1)).testDatabase(ETLTestHelper.ETL_DB_NAME);
    }

    @Test
    public void shouldReturnAllConfigsAsJson() throws Exception {
        //Given
        given(configService.allConfigs()).willReturn(configList);
        given(configResponseBuilder.createFrom(config1)).willReturn(ETLTestHelper.setUpConfigResponse());
        given(configService.getServices()).willReturn(ETLTestHelper.SERVICES);

        //When
        mockMvc.perform(get(CONFIGS_URL)).andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(ETLTestHelper.APPLICATION_JSON_UTF8))
                .andExpect(content().string(json(configResponseWrapper)));
        //Then
        verify(configService, times(1)).allConfigs();
        verify(configService, times(1)).getServices();
        verify(configResponseBuilder, times(1)).createFrom(config1);
    }

    @Test
    public void shouldCreateNewConfigsOrUpdateExistingConfigsAndReturnAllConfigsAsJson() throws Exception {
        //Given
        given(configService.allConfigs()).willReturn(configList);
        given(configService.getServices()).willReturn(ETLTestHelper.SERVICES);
        given(configResponseBuilder.createFrom(config1)).willReturn(ETLTestHelper.setUpConfigResponse());

        // When
        mockMvc.perform(post(CONFIGS_URL).contentType(MediaType.parseMediaType(ETLTestHelper.APPLICATION_JSON_UTF8))
                .content(json(configRequestWrapper).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(ETLTestHelper.APPLICATION_JSON_UTF8))
                .andExpect(content().string(json(configResponseWrapper)));
        //Then
        verify(configService, times(1)).allConfigs();
        verify(configService, times(1)).getServices();
        verify(configResponseBuilder, times(1)).createFrom(config1);
    }

    @Test
    public void shouldReturnStatusOkOnDeleteConfig() throws Exception {
        //Given, When and Then
        mockMvc.perform(delete(CONFIGS_URL + "{name}", ETLTestHelper.ETL_DB_NAME))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void shouldReturnStatusOkOnDeleteAllConfigs() throws Exception {
        //Given, When and Then
        mockMvc.perform(delete(CONFIGS_URL)).andExpect(status().is(HttpStatus.OK.value()));
    }
}

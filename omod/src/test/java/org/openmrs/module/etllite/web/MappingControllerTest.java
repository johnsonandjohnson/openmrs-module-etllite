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
import org.openmrs.module.etllite.api.builder.MappingRequestBuilder;
import org.openmrs.module.etllite.api.builder.MappingResponseBuilder;
import org.openmrs.module.etllite.api.contract.MappingRequest;
import org.openmrs.module.etllite.api.contract.MappingResponse;
import org.openmrs.module.etllite.api.domain.Mapping;
import org.openmrs.module.etllite.api.exception.ETLException;
import org.openmrs.module.etllite.api.exception.MappingAlreadyExistsException;
import org.openmrs.module.etllite.api.exception.MappingNotFoundException;
import org.openmrs.module.etllite.api.service.MappingService;
import org.openmrs.module.etllite.web.controller.MappingController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Mapping Controller Test Class
 *
 * @author nanakapa
 */
@RunWith(MockitoJUnitRunner.class)
public class MappingControllerTest extends BaseTest {

    private static final String MAPPINGS_URL = "/etllite/mappings/";

    private MockMvc mockMvc;

    private Mapping mapping;
    private Mapping dbMapping;
    private MappingRequest mappingRequest;
    private MappingResponse mappingResponse;
    private List<Mapping> mappings = new ArrayList<Mapping>();
    private List<MappingResponse> mappingResponseList = new ArrayList<>();

    @InjectMocks
    private MappingController mappingController = new MappingController();

    @Mock
    private MappingService mappingService;

    @Mock
    private MappingRequestBuilder mappingRequestBuilder;

    @Mock
    private MappingResponseBuilder mappingResponseBuilder;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mappingController).build();
        mapping = ETLTestHelper.setUpNewMapping();
        dbMapping = ETLTestHelper.setUpDbMapping();
        mappingRequest = ETLTestHelper.setUpMappingRequest();
        mappingResponse = ETLTestHelper.setUpMappingResponse();
    }

    @Test
    public void shouldCreateMappingAndReturnNewMappingAsJson() throws Exception {
        //Given
        given(mappingRequestBuilder.createFrom(isA(MappingRequest.class))).willReturn(mapping);
        given(mappingService.create(mapping)).willReturn(dbMapping);
        given(mappingResponseBuilder.createFrom(dbMapping)).willReturn(mappingResponse);

        // When
        mockMvc.perform(post(MAPPINGS_URL).contentType(MediaType.parseMediaType(ETLTestHelper.APPLICATION_JSON_UTF8))
                .content(json(mappingRequest).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(ETLTestHelper.APPLICATION_JSON_UTF8))
                .andExpect(content().string(json(mappingResponse)));
        //Then
        verify(mappingService, times(1)).create(mapping);
        verify(mappingRequestBuilder, times(1)).createFrom(isA(MappingRequest.class));
        verify(mappingResponseBuilder, times(1)).createFrom(mapping);
    }

    @Test
    public void shouldUpdateMappingAndReturnUpdatedMappingAsJson() throws Exception {
        //Given
        dbMapping.setLoadTemplate("updated load template");
        mappingResponse.setLoadTemplate("updated load template");
        given(mappingService.update(dbMapping)).willReturn(dbMapping);
        given(mappingRequestBuilder.createFrom(isA(MappingRequest.class))).willReturn(mapping);
        given(mappingResponseBuilder.createFrom(dbMapping)).willReturn(mappingResponse);

        // When
        mockMvc.perform(put(MAPPINGS_URL + ETLTestHelper.MAPPING_ID)
                .contentType(MediaType.parseMediaType(ETLTestHelper.APPLICATION_JSON_UTF8))
                .content(json(mappingRequest).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(ETLTestHelper.APPLICATION_JSON_UTF8))
                .andExpect(content().string(json(mappingResponse)));
        //Then
        verify(mappingService, times(1)).update(dbMapping);
        verify(mappingRequestBuilder, times(1)).createFrom(isA(MappingRequest.class));
        verify(mappingResponseBuilder, times(1)).createFrom(dbMapping);
    }

    @Test
    public void shouldDeleteMapping() throws Exception {
        //Given, When and Then
        mockMvc.perform(delete(MAPPINGS_URL + ETLTestHelper.MAPPING_ID, ETLTestHelper.MAPPING_ID))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void shouldReturnMappingAsJsonForTheSpecifiedMapping() throws Exception {
        mappingResponseList.add(mappingResponse);
        //Given
        given(mappingService.findById(dbMapping.getId())).willReturn(dbMapping);
        given(mappingResponseBuilder.createFrom(dbMapping)).willReturn(mappingResponse);

        //When
        mockMvc.perform(get(MAPPINGS_URL)
                .param("lookup", "By Id")
                .param("id", ETLTestHelper.MAPPING_ID.toString()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(ETLTestHelper.APPLICATION_JSON_UTF8))
                .andExpect(content().string(json(mappingResponseList)));
        //Then
        verify(mappingService, times(1)).findById(dbMapping.getId());
        verify(mappingResponseBuilder, times(1)).createFrom(dbMapping);
    }

    @Test
    public void shouldReturnMappingsAsJsonForASpecifiedETLSource() throws Exception {
        mappings.add(dbMapping);
        mappingResponseList.add(mappingResponse);
        //Given
        given(mappingService.findBySource(ETLTestHelper.MAPPING_SOURCE)).willReturn(mappings);
        given(mappingResponseBuilder.createFrom(dbMapping)).willReturn(mappingResponse);

        //When
        mockMvc.perform(get(MAPPINGS_URL)
                .param("lookup", "By Source")
                .param("source", ETLTestHelper.MAPPING_SOURCE))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(ETLTestHelper.APPLICATION_JSON_UTF8))
                .andExpect(content().string(json(mappingResponseList)));
        //Then
        verify(mappingResponseBuilder, times(1)).createFrom(dbMapping);
        verify(mappingService, times(1)).findBySource(dbMapping.getSource());
    }

    @Test
    public void shouldReturnJsonForTheSpecifiedMappingWhenTestMappingIsInvoked() throws Exception {

        Map<String, List<Map<String, Object>>> responseMap = new HashMap<>();
        //Given
        given(mappingService.testMapping(dbMapping.getId())).willReturn(responseMap);

        //When
        mockMvc.perform(get(MAPPINGS_URL + dbMapping.getId() + "/test")).andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(ETLTestHelper.APPLICATION_JSON_UTF8))
                .andExpect(content().string(json(responseMap)));
        //Then
        verify(mappingService, times(1)).testMapping(dbMapping.getId());

    }

    @Test
    public void shouldReturnBadRequestIfMappingOrSourceIsNull() throws Exception {
        //Given
        mapping.setName(null);
        given(mappingRequestBuilder.createFrom(isA(MappingRequest.class))).willReturn(mapping);
        given(mappingService.create(mapping)).willThrow(new IllegalArgumentException());

        // When
        mockMvc.perform(post(MAPPINGS_URL)
                .contentType(MediaType.parseMediaType(ETLTestHelper.APPLICATION_JSON_UTF8))
                .content(json(mappingRequest).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        //Then
        verify(mappingService, times(1)).create(mapping);
        verify(mappingRequestBuilder, times(1)).createFrom(isA(MappingRequest.class));
    }

    @Test
    public void shouldReturnConflictStatusIfMappingAlreadyExistsWhenCreateANewMapping() throws Exception {
        //Given
        mapping.setName(null);
        given(mappingRequestBuilder.createFrom(isA(MappingRequest.class))).willReturn(mapping);
        given(mappingService.create(mapping)).willThrow(new MappingAlreadyExistsException("Mapping Already Exists"));

        // When
        mockMvc.perform(post(MAPPINGS_URL)
                .contentType(MediaType.parseMediaType(ETLTestHelper.APPLICATION_JSON_UTF8))
                .content(json(mappingRequest).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().is(HttpStatus.CONFLICT.value()));

        //Then
        verify(mappingService, times(1)).create(mapping);
        verify(mappingRequestBuilder, times(1)).createFrom(isA(MappingRequest.class));
    }

    @Test
    public void shouldReturn404WhenTryToUpdateANonExistingMapping() throws Exception {
        //Given
        dbMapping.setLoadTemplate("updated load template");
        mappingResponse.setLoadTemplate("updated load template");
        given(mappingRequestBuilder.createFrom(isA(MappingRequest.class))).willReturn(mapping);
        given(mappingResponseBuilder.createFrom(dbMapping)).willReturn(mappingResponse);
        given(mappingService.update(dbMapping)).willThrow(new MappingNotFoundException("Mapping does not exists"));

        // When
        mockMvc.perform(put(MAPPINGS_URL + ETLTestHelper.MAPPING_ID)
                .contentType(MediaType.parseMediaType(ETLTestHelper.APPLICATION_JSON_UTF8))
                .content(json(mappingRequest).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        //Then
        verify(mappingService, times(1)).update(dbMapping);
        verify(mappingRequestBuilder, times(1)).createFrom(isA(MappingRequest.class));
    }

    @Test
    public void shouldReturnInternalServerErrorForScriptErrorWhenTestMappingIsInvoked() throws Exception {

        Map<String, List<Map<String, Object>>> responseMap = new HashMap<>();
        //Given a velocity directive that's missing a #end
        dbMapping.setTransformTemplate("#if ($x) we won't close!");
        given(mappingService.testMapping(dbMapping.getId())).willThrow(new ETLException("", isA(IOException.class)));

        //When
        mockMvc.perform(get(MAPPINGS_URL + dbMapping.getId() + "/test"))
                .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        //Then
        verify(mappingService, times(1)).testMapping(dbMapping.getId());

    }
}

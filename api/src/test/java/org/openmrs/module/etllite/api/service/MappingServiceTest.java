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

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.etllite.api.BaseTest;
import org.openmrs.module.etllite.api.ETLTestHelper;
import org.openmrs.module.etllite.api.constants.Constants;
import org.openmrs.module.etllite.api.dao.MappingDao;
import org.openmrs.module.etllite.api.domain.Mapping;
import org.openmrs.module.etllite.api.event.ETLEvent;
import org.openmrs.module.etllite.api.exception.ETLException;
import org.openmrs.module.etllite.api.exception.MappingAlreadyExistsException;
import org.openmrs.module.etllite.api.exception.MappingNotFoundException;
import org.openmrs.module.etllite.api.service.impl.MappingServiceImpl;
import org.openmrs.module.etllite.api.task.ETLJobTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * MappingService Test Class
 *
 * @author nanakapa
 */
@RunWith(MockitoJUnitRunner.class)
public class MappingServiceTest extends BaseTest {

    private Mapping mapping;

    private Mapping dbMapping;

    private ETLEvent event;

    private Map<String, Object> eventParams;

    @InjectMocks
    private MappingService mappingService = new MappingServiceImpl();

    @Mock
    private MappingDao mappingDao;

    @Mock
    private ETLService etlService;

    @Mock
    private ETLSchedulerService schedulerService;

    @Before
    public void setUp() {
        mapping = ETLTestHelper.setUpNewMapping();
        dbMapping = ETLTestHelper.setUpDbMapping();
        eventParams = new HashMap<>();
    }

    @Test
    public void shouldCreateNewMapping() throws MappingAlreadyExistsException, MappingNotFoundException {
        //Given
        ArgumentCaptor<ETLEvent> eventArgumentCaptor = ArgumentCaptor.forClass(ETLEvent.class);
        given(mappingDao.findByNameAndSource(mapping.getName(), mapping.getSource())).willReturn(null);
        given(mappingDao.create(mapping)).willReturn(dbMapping);

        //When
        Mapping newMapping = mappingService.create(mapping);

        eventParams.put(Constants.PARAM_JOB_ID, newMapping.getSource() + Constants.SUBJECT_SEPARATOR + newMapping.getName());
        eventParams.put(Constants.PARAM_MAPPING, newMapping.getName());
        eventParams.put(Constants.PARAM_SOURCE, mapping.getSource());
        event = new ETLEvent(Constants.SUBJECT_RUNNER, eventParams);

        //Then
        assertNotNull(newMapping);
        ETLTestHelper.checkMappingAsserts(mapping, newMapping);

        verify(mappingDao, times(1)).findByNameAndSource(dbMapping.getName(), dbMapping.getSource());
        verify(mappingDao, times(1)).create(mapping);
        verify(schedulerService, times(1)).safeScheduleJob(eventArgumentCaptor.capture(),
                eq(mapping.getCronExpression()), notNull(ETLJobTask.class));
        checkScheduleCronAsserts(eventArgumentCaptor);
    }

    @Test
    public void shouldThrowMappingAlreadyExistsIfMappingIsAddedWithDuplicateNameAndSource()
            throws MappingAlreadyExistsException, MappingNotFoundException {

        expectedException.expect(MappingAlreadyExistsException.class);
        //Given
        given(mappingDao.findByNameAndSource(mapping.getName(), mapping.getSource())).willReturn(dbMapping);
        given(mappingDao.create(mapping)).willReturn(dbMapping);

        //When
        Mapping newMapping = null;
        try {
            newMapping = mappingService.create(mapping);
        } finally {
            //Then
            assertNull(newMapping);
            verify(mappingDao, times(1)).findByNameAndSource(dbMapping.getName(), dbMapping.getSource());
            verify(mappingDao, never()).create(mapping);
        }
    }

    @Test
    public void shouldThrowIllegalArgumentIfMappingOrSourceIsNull() throws MappingAlreadyExistsException,
            MappingNotFoundException {

        expectedException.expect(IllegalArgumentException.class);
        mapping.setName(null);

        //Given
        given(mappingDao.create(mapping)).willReturn(dbMapping);

        //When
        Mapping newMapping = null;
        try {
            newMapping = mappingService.create(mapping);
        } finally {
            //Then
            assertNull(newMapping);
            verifyZeroInteractions(mappingDao);
        }
    }

    @Test
    public void shouldUpdateMapping() throws MappingNotFoundException {
        //Given
        Mapping mappingToBeUpdated = ETLTestHelper.setUpDbMapping();
        mappingToBeUpdated.setName("updatedName");

        mapping.setId(ETLTestHelper.MAPPING_ID);
        mapping.setLoadTemplate("Updated Load Template");

        given(mappingDao.findById(mappingToBeUpdated.getId())).willReturn(dbMapping);
        given(mappingDao.update(mappingToBeUpdated)).willReturn(mappingToBeUpdated);

        //When
        Mapping updatedMapping = mappingService.update(mappingToBeUpdated);

        //Then
        assertNotNull(updatedMapping);
        ETLTestHelper.checkMappingAsserts(mappingToBeUpdated, updatedMapping);
        verify(mappingDao, times(1)).update(mappingToBeUpdated);
        verify(mappingDao, times(1)).findById(mappingToBeUpdated.getId());
    }

    @Test
    public void shouldRescheduleJobWhenCronExpressionIsNotNullWhenUpdateMappingIsInvoked() throws MappingNotFoundException {

        ArgumentCaptor<ETLEvent> schedulableJobArgumentCaptor = ArgumentCaptor.forClass(ETLEvent.class);
        //Given
        Mapping mappingToBeUpdated = ETLTestHelper.setUpDbMapping();
        mappingToBeUpdated.setName("updatedName");

        mapping.setId(ETLTestHelper.MAPPING_ID);
        mapping.setLoadTemplate("Updated Load Template");

        given(mappingDao.findById(mappingToBeUpdated.getId())).willReturn(dbMapping);
        given(mappingDao.update(mappingToBeUpdated)).willReturn(mappingToBeUpdated);

        //When
        Mapping updatedMapping = mappingService.update(mappingToBeUpdated);

        eventParams.put(Constants.PARAM_JOB_ID,
                updatedMapping.getSource() + Constants.SUBJECT_SEPARATOR + updatedMapping.getName());
        eventParams.put(Constants.PARAM_MAPPING, updatedMapping.getName());
        eventParams.put(Constants.PARAM_SOURCE, mapping.getSource());
        event = new ETLEvent(Constants.SUBJECT_RUNNER, eventParams);

        //Then
        assertNotNull(updatedMapping);
        ETLTestHelper.checkMappingAsserts(mappingToBeUpdated, updatedMapping);
        verify(mappingDao, times(1)).update(mappingToBeUpdated);
        verify(mappingDao, times(1)).findById(mappingToBeUpdated.getId());

        verify(schedulerService, times(1)).safeUnscheduleJob(Constants.SUBJECT_RUNNER,
                generateJobId(mappingToBeUpdated.getSource(), mappingToBeUpdated.getName()));
        verify(schedulerService, times(1)).safeScheduleJob(schedulableJobArgumentCaptor.capture(),
                eq(mapping.getCronExpression()), notNull(ETLJobTask.class));
        checkScheduleCronAsserts(schedulableJobArgumentCaptor);
    }

    @Test
    public void shouldUnScheduleJobWhenCronExpressionIsNullWhenUpdateMappingIsInvoked() throws MappingNotFoundException {
        //Given
        Mapping mappingToBeUpdated = ETLTestHelper.setUpDbMapping();
        mappingToBeUpdated.setName("updatedName");

        mapping.setId(ETLTestHelper.MAPPING_ID);
        mapping.setLoadTemplate("Updated Load Template");
        mapping.setCronExpression(null);

        given(mappingDao.findById(mappingToBeUpdated.getId())).willReturn(dbMapping);
        given(mappingDao.update(mappingToBeUpdated)).willReturn(mappingToBeUpdated);

        //When
        Mapping updatedMapping = mappingService.update(mappingToBeUpdated);

        eventParams.put(Constants.PARAM_JOB_ID,
                updatedMapping.getSource() + Constants.SUBJECT_SEPARATOR + updatedMapping.getName());
        eventParams.put(Constants.PARAM_MAPPING, updatedMapping.getName());
        event = new ETLEvent(Constants.SUBJECT_RUNNER, eventParams);

        //Then
        assertNotNull(updatedMapping);
        ETLTestHelper.checkMappingAsserts(mappingToBeUpdated, updatedMapping);
        verify(mappingDao, times(1)).update(mappingToBeUpdated);
        verify(mappingDao, times(1)).findById(mappingToBeUpdated.getId());

        verify(schedulerService, times(1)).safeUnscheduleJob(Constants.SUBJECT_RUNNER,
                generateJobId(mappingToBeUpdated.getSource(), mappingToBeUpdated.getName()));
    }

    @Test
    public void shouldThrowMappingNotFoundIfWeTryToUpdateNonExistingMapping() throws MappingNotFoundException {
        mapping.setId(ETLTestHelper.NON_EXISTING_MAPPING_ID);
        expectedException.expect(MappingNotFoundException.class);
        //Given
        given(mappingDao.findById(ETLTestHelper.NON_EXISTING_MAPPING_ID)).willReturn(null);

        //When
        try {
            mappingService.update(mapping);
        } finally {
            verify(mappingDao, times(1)).findById(ETLTestHelper.NON_EXISTING_MAPPING_ID);
            verify(mappingDao, never()).update(dbMapping);
        }
    }

    @Test
    public void shouldDeleteMapping() throws MappingNotFoundException {
        //Given
        given(mappingDao.findById(1)).willReturn(dbMapping);

        //When
        mappingService.delete(dbMapping.getId());

        //Then
        verify(mappingDao, times(1)).findById(dbMapping.getId());
        verify(mappingDao, times(1)).delete(dbMapping);
        verify(schedulerService, times(1)).safeUnscheduleJob(Constants.SUBJECT_RUNNER,
                generateJobId(dbMapping.getSource(), dbMapping.getName()));
    }

    @Test
    public void shouldThrowMappingNotFoundIfWeTryToDeleteNonExistingMapping() throws MappingNotFoundException {
        expectedException.expect(MappingNotFoundException.class);
        //Given
        given(mappingDao.findById(ETLTestHelper.NON_EXISTING_MAPPING_ID)).willReturn(null);

        //When
        try {
            mappingService.delete(dbMapping.getId());
        } finally {
            verify(mappingDao, times(1)).findById(dbMapping.getId());
            verify(mappingDao, never()).delete(dbMapping);
        }
    }

    @Test
    public void shouldReturnMappingForTheSpecifiedMappingId() {
        //Given
        given(mappingDao.findById(dbMapping.getId())).willReturn(dbMapping);

        //When
        Mapping existingMapping = mappingService.findById(dbMapping.getId());

        //Then
        assertNotNull(existingMapping);
        ETLTestHelper.checkMappingAsserts(dbMapping, existingMapping);
        verify(mappingDao, times(1)).findById(dbMapping.getId());
    }

    @Test
    public void shouldReturnMappingsForTheSpecifiedETLDatabase() {
        List<Mapping> mappings = new ArrayList<>();
        mappings.add(dbMapping);

        //Given
        given(mappingDao.findBySource(ETLTestHelper.MAPPING_SOURCE)).willReturn(mappings);

        //When
        List<Mapping> dbMappings = mappingService.findBySource(ETLTestHelper.MAPPING_SOURCE);

        //Then
        assertThat(dbMappings.size(), is(1));
        ETLTestHelper.checkMappingAsserts(mapping, dbMappings.get(0));

        verify(mappingDao, times(1)).findBySource(ETLTestHelper.MAPPING_SOURCE);
    }

    @Test
    public void shouldReturnAllMappings() {
        List<Mapping> mappings = new ArrayList<>();
        mappings.add(dbMapping);

        //Given
        given(mappingDao.retrieveAll()).willReturn(mappings);

        //When
        List<Mapping> dbMappings = mappingService.findAll();

        //Then
        assertThat(dbMappings.size(), is(1));
        ETLTestHelper.checkMappingAsserts(mapping, dbMappings.get(0));

        verify(mappingDao, times(1)).retrieveAll();
    }

    @Test
    public void shouldTestMapping() throws MappingNotFoundException, ETLException, IOException {

        Map<String, Object> params = new HashMap<>();
        params.put("source", ETLTestHelper.MAPPING_SOURCE);

        List<Map<String, Object>> extractedData = new ArrayList<>();
        Map<String, Object> extractMap = new HashMap<>();
        extractMap.put("patientId", 1);
        extractMap.put("age", 15);
        extractMap.put("gender", "Male");
        extractedData.add(extractMap);

        List<Map<String, Object>> transformedData = new ArrayList<>();
        Map<String, Object> transformMap = new HashMap<>();
        transformMap.put("patientId", 1);
        transformMap.put("age", 15);
        transformMap.put("gender", "male");
        transformedData.add(transformMap);

        //Given
        given(mappingDao.findById(dbMapping.getId())).willReturn(dbMapping);

        given(etlService.extract(dbMapping.getName(), params)).willReturn(extractedData);
        given(etlService.transform(dbMapping.getName(), params, extractedData)).willReturn(transformedData);

        //When
        Map<String, List<Map<String, Object>>> testData = mappingService.testMapping(dbMapping.getId());

        //Then
        assertNotNull(testData);
        assertThat(testData.get("extracted").get(0).get("patientId").toString(), Matchers.equalTo("1"));
        assertThat(testData.get("extracted").get(0).get("age").toString(), Matchers.equalTo("15"));
        assertThat(testData.get("extracted").get(0).get("gender").toString(), Matchers.equalTo("Male"));
        assertThat(testData.get("transformed").get(0).get("patientId").toString(), Matchers.equalTo("1"));
        assertThat(testData.get("transformed").get(0).get("age").toString(), Matchers.equalTo("15"));
        assertThat(testData.get("transformed").get(0).get("gender").toString(), Matchers.equalTo("male"));
    }

    @Test
    public void shouldThrowMappingNotFoundIfTheMappingIsMissedInDatabaseWhenTestMappingIsCalled()
            throws MappingNotFoundException, ETLException, IOException {
        expectedException.expect(MappingNotFoundException.class);
        mappingService.testMapping(null);
    }

    private void checkScheduleCronAsserts(ArgumentCaptor<ETLEvent> eventArgumentCaptor) {
        assertThat(eventArgumentCaptor.getValue(), Matchers.equalTo(event));
        assertThat(eventArgumentCaptor.getValue().getParameters(),
                Matchers.equalTo(event.getParameters()));
        assertThat(eventArgumentCaptor.getValue().getSubject(),
                Matchers.equalTo(event.getSubject()));
        assertThat(eventArgumentCaptor.getValue().getParameters().get(Constants.PARAM_JOB_ID),
                Matchers.equalTo(event.getParameters().get(Constants.PARAM_JOB_ID)));
    }

    private String generateJobId(String source, String mapping) {
        return new StringBuilder().append(source).append("-").append(mapping).toString();
    }
}

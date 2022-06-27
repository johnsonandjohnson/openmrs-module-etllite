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
import org.openmrs.module.etllite.api.dao.MappingDao;
import org.openmrs.module.etllite.api.domain.Config;
import org.openmrs.module.etllite.api.domain.Mapping;
import org.openmrs.module.etllite.api.exception.ETLException;
import org.openmrs.module.etllite.api.exception.MappingAlreadyExistsException;
import org.openmrs.module.etllite.api.exception.MappingNotFoundException;
import org.openmrs.module.etllite.api.service.ConfigService;
import org.openmrs.module.etllite.api.service.MappingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * MappingService Integration Test Class
 *
 * @author nanakapa
 */
public class MappingServiceBundleIT extends BaseETLContextSensitiveTest {

    // location_id -> person_uuid
    private static final Integer LOCATION_ID = 1;
    private static final String MAPPED_PERSON_UUID = LOCATION_ID.toString();

    // location_date_created -> person_birthdate
    private static final Date LOCATION_DATE_CREATED = new Date(1127340000000L);
    private static final String MAPPED_PERSON_DATE_OF_BIRTH = "09/22/2005";

    // location_name -> person_gender
    private static final String LOCATION_NAME = "Unknown Location";
    private static final String MAPPED_PERSON_GENDER = "female";

    private static final String PARAM_EXTERNAL_ID = "externalId";
    private static final String PARAM_GENDER = "gender";
    private static final String PARAM_DATE_OF_BIRTH = "dateOfBirth";

    private Mapping mapping;
    private Config config;

    @Autowired
    private ConfigService configService;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private MappingDao mappingDao;

    @Before
    public void setUp() {
        //Given
        mapping = ETLTestHelper.setUpNewMapping();
        setUpDatabase();
    }

    @Test
    public void shouldCreateNewMapping() throws MappingAlreadyExistsException, MappingNotFoundException {
        //When
        mappingService.create(mapping);

        //Then
        Mapping newMapping = mappingDao.findByNameAndSource(mapping.getName(), mapping.getSource());
        assertNotNull(newMapping);
        ETLTestHelper.checkMappingAsserts(mapping, newMapping);
    }

    @Test
    public void shouldUpdateMapping() throws MappingAlreadyExistsException, MappingNotFoundException {
        //Given
        Mapping newMapping = mappingService.create(mapping);
        assertNotNull(newMapping);
        newMapping.setLoadTemplate("Updated Load Template");

        //When
        mappingService.update(newMapping);

        //Then
        Mapping updatedMapping = mappingDao.findById(newMapping.getId());
        assertNotNull(updatedMapping);
        ETLTestHelper.checkMappingAsserts(newMapping, updatedMapping);
    }

    @Test
    public void shouldDeleteMapping() throws MappingAlreadyExistsException, MappingNotFoundException {
        //Given
        Mapping newMapping = mappingService.create(mapping);
        assertNotNull(newMapping);

        //When
        mappingService.delete(newMapping.getId());
        assertNull(mappingDao.findById(newMapping.getId()));
    }

    @Test(expected = MappingNotFoundException.class)
    public void shouldThrowMappingNotFoundIfWeTryToDeleteNonExistingMapping() throws MappingNotFoundException {
        mappingService.delete(ETLTestHelper.NON_EXISTING_MAPPING_ID);
    }

    @Test
    public void shouldReturnMappingForTheSpecifiedMappingId() throws MappingAlreadyExistsException, MappingNotFoundException {
        //Given
        Mapping newMapping = mappingService.create(mapping);
        assertNotNull(newMapping);

        //When
        Mapping existingMapping = mappingService.findById(newMapping.getId());

        //Then
        assertNotNull(existingMapping);
        ETLTestHelper.checkMappingAsserts(newMapping, existingMapping);
    }

    @Test
    public void shouldReturnMappingForTheSpecifiedMappingName() throws MappingAlreadyExistsException,
            MappingNotFoundException {
        //Given
        Mapping newMapping = mappingService.create(mapping);
        assertNotNull(newMapping);

        //When
        Mapping existingMapping = mappingDao.findByNameAndSource(newMapping.getName(), newMapping.getSource());

        //Then
        assertNotNull(existingMapping);
        ETLTestHelper.checkMappingAsserts(newMapping, existingMapping);
    }

    @Test
    public void shouldReturnMappingsForTheSpecifiedETLDatabase() throws MappingAlreadyExistsException,
            MappingNotFoundException {
        Mapping newMapping = mappingService.create(mapping);
        assertNotNull(newMapping);

        //When
        List<Mapping> dbMappings = mappingService.findBySource(newMapping.getSource());

        //Then
        assertThat(dbMappings.size(), is(1));
        ETLTestHelper.checkMappingAsserts(newMapping, dbMappings.get(0));
    }

    @Test
    public void shouldReturnAllMappings() throws MappingAlreadyExistsException, MappingNotFoundException {
        Mapping newMapping = mappingService.create(mapping);
        assertNotNull(newMapping);

        //When
        List<Mapping> dbMappings = mappingService.findAll();

        //Then
        assertThat(dbMappings.size(), is(1));
        ETLTestHelper.checkMappingAsserts(newMapping, dbMappings.get(0));
    }

    @Test
    public void shouldTestMapping() throws MappingAlreadyExistsException, ETLException, MappingNotFoundException,
            IOException {

        createETLDatabase();
        mapping.setQuery(ETLTestHelper.MAPPING_BATCH_QUERY);
        Mapping newMapping = mappingService.create(mapping);
        assertNotNull(newMapping);

        //When
        Map<String, List<Map<String, Object>>> testData = mappingService.testMapping(newMapping.getId());

        //Then
        assertNotNull(testData);
        assertThat(testData.get("extracted").get(0).get("location_id").toString(), is(LOCATION_ID.toString()));
        assertThat(testData.get("extracted").get(0).get("name").toString(), is(LOCATION_NAME));
        assertThat(((Date)testData.get("extracted").get(0).get("date_created")).toInstant(),
            is(LOCATION_DATE_CREATED.toInstant()));
        assertThat(testData.get("transformed").get(0).get(PARAM_EXTERNAL_ID).toString(), is(MAPPED_PERSON_UUID));
        assertThat(testData.get("transformed").get(0).get(PARAM_GENDER).toString(), is(MAPPED_PERSON_GENDER));
        assertThat(testData.get("transformed").get(0).get(PARAM_DATE_OF_BIRTH).toString(), is(MAPPED_PERSON_DATE_OF_BIRTH));
    }

    @After
    public void tearDown() {
        mappingDao.deleteAll();
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
}

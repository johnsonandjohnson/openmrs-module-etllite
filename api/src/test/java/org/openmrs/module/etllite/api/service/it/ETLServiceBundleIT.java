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
import org.openmrs.Person;
import org.openmrs.api.db.PersonDAO;
import org.openmrs.module.etllite.api.ETLTestHelper;
import org.openmrs.module.etllite.api.contract.ConfigRequestWrapper;
import org.openmrs.module.etllite.api.dao.ETLLogDao;
import org.openmrs.module.etllite.api.dao.MappingDao;
import org.openmrs.module.etllite.api.domain.Config;
import org.openmrs.module.etllite.api.domain.ETLLog;
import org.openmrs.module.etllite.api.domain.Mapping;
import org.openmrs.module.etllite.api.exception.ETLException;
import org.openmrs.module.etllite.api.exception.MappingAlreadyExistsException;
import org.openmrs.module.etllite.api.exception.MappingNotFoundException;
import org.openmrs.module.etllite.api.service.ConfigService;
import org.openmrs.module.etllite.api.service.ETLService;
import org.openmrs.module.etllite.api.service.MappingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * ETLService Integration Test Class
 *
 * @author nanakapa
 */
public class ETLServiceBundleIT extends BaseETLContextSensitiveTest {

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
    private Map<String, Object> params = new HashMap<>();

    @Autowired
    private ETLService etlService;

    private Config config;

    @Autowired
    private ConfigService configService;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private MappingDao mappingDao;

    @Autowired
    private ETLLogDao etlLogDao;

    @Autowired
    private PersonDAO personDAO;

    @Before
    public void setUp() throws MappingAlreadyExistsException {
        mapping = ETLTestHelper.setUpNewMapping();
        Mapping dbMapping = mappingService.create(mapping);
        params.put("locationId", LOCATION_ID);
        params.put("source", dbMapping.getSource());

        setUpDatabase();
        createETLDatabase();
    }

    @Test
    public void shouldExtractData() throws MappingNotFoundException, IOException {
        List<Map<String, Object>> rows = etlService.extract(mapping.getName(), params);
        assertNotNull(rows);
        assertThat(rows.size(), is(1));
        assertThat(rows.get(0).get("location_id").toString(), is(LOCATION_ID.toString()));
        assertThat(rows.get(0).get("name").toString(), is(LOCATION_NAME));
        assertThat(((Date) rows.get(0).get("date_created")).toInstant(), is(LOCATION_DATE_CREATED.toInstant()));
    }

    @Test
    public void shouldTransformData() throws ETLException, MappingNotFoundException, IOException {
        //Given
        List<Map<String, Object>> rows = etlService.extract(mapping.getName(), params);
        assertNotNull(rows);
        //When
        List<Map<String, Object>> outs = etlService.transform(mapping.getName(), params, rows);

        assertNotNull(outs);
        assertThat(outs.size(), is(1));
        assertThat(outs.get(0).get(PARAM_EXTERNAL_ID).toString(), is(MAPPED_PERSON_UUID));
        assertThat(outs.get(0).get(PARAM_GENDER).toString(), is(MAPPED_PERSON_GENDER));
        assertThat(outs.get(0).get(PARAM_DATE_OF_BIRTH).toString(), is(MAPPED_PERSON_DATE_OF_BIRTH));
    }

    @Test
    public void shouldLoadData() throws ETLException, MappingNotFoundException, IOException {
        //Given
        List<Map<String, Object>> rows = etlService.extract(mapping.getName(), params);
        assertNotNull(rows);

        List<Map<String, Object>> outs = etlService.transform(mapping.getName(), params, rows);
        assertNotNull(outs);

        //When
        etlService.load(mapping.getName(), params, rows, outs, 150);

        //Then
        Person person = personDAO.getPersonByUuid(MAPPED_PERSON_UUID);
        checkAsserts(person);
    }

    @Test
    public void shouldDoETL() throws ETLException {
        //When
        etlService.doETL(mapping.getName(), params);

        //Then
        Person person = personDAO.getPersonByUuid(MAPPED_PERSON_UUID);
        checkAsserts(person);
    }

    @Test
    public void shouldCreateLogWhenDoETLIsInvoked() throws ETLException {
        //When
        etlService.doETL(mapping.getName(), params);

        //Then
        Person person = personDAO.getPersonByUuid(MAPPED_PERSON_UUID);
        checkAsserts(person);

        assertThat(etlLogDao.retrieveAll().size(), is(1));
        ETLLog etlLog = etlLogDao.retrieveAll().get(0);
        assertThat(etlLog.getMapping(), is(mapping.getName()));
        assertThat(etlLog.getDatabaseName(), is(mapping.getSource()));
        assertThat(etlLog.getExtractedRecords(), is(1));
        assertThat(etlLog.getTransformedRecords(), is(1));
    }

    @After
    public void tearDown() {
        mappingDao.deleteAll();
        configService.deleteAll();
        etlLogDao.deleteAll();
    }

    private void createETLDatabase() {
        List<Config> configs = new ArrayList<>();
        configs.add(config);
        ConfigRequestWrapper configRequestWrapper = new ConfigRequestWrapper();
        configRequestWrapper.setDatabases(configs);
        configRequestWrapper.setServices(ETLTestHelper.SERVICES);
        configService.createOrUpdateConfigs(configRequestWrapper);
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

    private void checkAsserts(Person person) {
        assertThat(person.getGender(), is(MAPPED_PERSON_GENDER));
        // instead of using person.birthdate, personvoidreason is used, because this field is string and we want
        // verify if date is properly parsed to string with formatdate
        assertThat(person.getPersonVoidReason(), is(MAPPED_PERSON_DATE_OF_BIRTH));
        assertThat(person.getUuid(), is(MAPPED_PERSON_UUID));
    }
}

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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.etllite.api.BaseTest;
import org.openmrs.module.etllite.api.ETLTestHelper;
import org.openmrs.module.etllite.api.dao.ETLLogDao;
import org.openmrs.module.etllite.api.dao.MappingDao;
import org.openmrs.module.etllite.api.domain.ETLLog;
import org.openmrs.module.etllite.api.domain.Mapping;
import org.openmrs.module.etllite.api.exception.ETLException;
import org.openmrs.module.etllite.api.exception.MappingAlreadyExistsException;
import org.openmrs.module.etllite.api.exception.MappingNotFoundException;
import org.openmrs.module.etllite.api.service.impl.ETLServiceImpl;
import org.openmrs.module.etllite.api.util.DateUtil;
import org.openmrs.module.etllite.api.util.Util;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * ETL Service Unit Test Class
 *
 * @author nanakapa
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ServiceContext.class, DateUtil.class})
public class ETLServiceTest extends BaseTest {

    private static final String BEAN_NAME = "patientDataService";
    private Mapping mapping;
    private Mapping dbMapping;
    private Date dateTime;
    private ETLLog etlLog;

    private Map<String, Object> params = new HashMap<>();

    @InjectMocks
    private ETLService etlService = new ETLServiceImpl();

    @Mock
    private MappingService mappingService;

    @Mock
    private MappingDao mappingDao;

    @Mock
    private ConfigService configService;

    @Mock
    private DriverManagerDataSource dataSource;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Mock
    private Util util;

    @Mock
    private ETLLogDao etlLogDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(ETLServiceTest.class);
        PowerMockito.mockStatic(DateUtil.class);
        PowerMockito.mockStatic(ServiceContext.class);
        given(ServiceContext.getInstance()).willReturn(mock(ServiceContext.class));
        given(ServiceContext.getInstance().getApplicationContext()).willReturn(mock(ApplicationContext.class));

        dateTime = createDate(2010, Calendar.NOVEMBER, 16, 15, 43, 59);
        //Given
        BDDMockito.given(DateUtil.now()).willReturn(dateTime);
        mapping = ETLTestHelper.setUpNewMapping();
        dbMapping = ETLTestHelper.setUpDbMapping();

        etlLog = new ETLLog();
        etlLog.setDatabaseName(mapping.getSource());
        etlLog.setMapping(mapping.getName());

        params.put("patientId", "123");
        params.put("source", ETLTestHelper.MAPPING_SOURCE);
        ((ETLServiceImpl) etlService).initialize();
    }

    @Test
    public void shouldExtractData()
            throws MappingAlreadyExistsException, SQLException, MappingNotFoundException, IOException {
        //Given
        given(mappingDao.findByNameAndSource(mapping.getName(), mapping.getSource())).willReturn(dbMapping);
        given(configService.getDataSource(ETLTestHelper.MAPPING_SOURCE)).willReturn(dataSource);
        given(util.getNamedParameterJdbcTemplate(dataSource, ETLTestHelper.DEFAULT_FETCH_SIZE))
                .willReturn(namedParameterJdbcTemplate);

        //When
        etlService.extract(mapping.getName(), params);

        //Then
        verify(mappingDao, times(1)).findByNameAndSource(mapping.getName(), mapping.getSource());
        verify(configService, times(1)).getDataSource(ETLTestHelper.MAPPING_SOURCE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfMappingIsNullWhenExtractIsCalled()
            throws MappingNotFoundException, IOException {
        //Given
        mapping.setName(null);
        //When
        etlService.extract(mapping.getName(), params);
    }

    @Test
    public void shouldTransformData() throws ETLException, MappingNotFoundException {

        //Given
        List<Map<String, Object>> rows = new ArrayList<>();
        given(mappingDao.findByNameAndSource(mapping.getName(), mapping.getSource())).willReturn(dbMapping);

        //When
        etlService.transform(mapping.getName(), params, rows);

        //Verify
        verify(mappingDao, times(1)).findByNameAndSource(mapping.getName(), mapping.getSource());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfMappingIsNullWhenTransformIsCalled()
            throws ETLException, MappingNotFoundException {
        //Given
        List<Map<String, Object>> rows = new ArrayList<>();
        mapping.setName(null);
        //When
        etlService.transform(mapping.getName(), params, rows);
    }

    @Test(expected = MappingNotFoundException.class)
    public void shouldThrowMappingNotFoundIfMappingIsMissingInDatabase()
            throws ETLException, MappingNotFoundException, IOException {
        //Given
        given(mappingDao.findByNameAndSource(dbMapping.getName(), dbMapping.getSource())).willReturn(null);

        //When
        etlService.extract(dbMapping.getName(), params);
    }

    @Test
    public void shouldLoadData() throws ETLException, MappingNotFoundException {
        Map<String, String> services = new HashMap<>();
        services.put("patientSrvc", "patientDataService");
        //Given
        List<Map<String, Object>> rows = new ArrayList<>();
        List<Map<String, Object>> outs = new ArrayList<>();
        given(mappingDao.findByNameAndSource(mapping.getName(), mapping.getSource())).willReturn(dbMapping);
        given(configService.getServices()).willReturn(ETLTestHelper.SERVICES);
        given(util.parseStringToMap(configService.getServices())).willReturn(services);
        given(ServiceContext.getInstance().getApplicationContext().getBean(BEAN_NAME)).willReturn(BEAN_NAME);
        //When
        etlService.load(mapping.getName(), params, rows, outs, etlLog.getId());

        //Verify
        verify(mappingDao, times(1)).findByNameAndSource(mapping.getName(), mapping.getSource());
        verify(configService, times(2)).getServices();
        verify(util, times(1)).parseStringToMap(configService.getServices());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfServiceNotFound() throws ETLException, MappingNotFoundException {
        Map<String, String> services = new HashMap<>();
        services.put("patientSrvc", BEAN_NAME);
        //Given
        List<Map<String, Object>> rows = new ArrayList<>();
        List<Map<String, Object>> outs = new ArrayList<>();
        given(mappingDao.findByNameAndSource(mapping.getName(), mapping.getSource())).willReturn(dbMapping);
        given(configService.getServices()).willReturn(ETLTestHelper.SERVICES);
        given(util.parseStringToMap(configService.getServices())).willReturn(services);
        given(ServiceContext.getInstance().getApplicationContext().getBean(BEAN_NAME)).willThrow(
                new NoSuchBeanDefinitionException("Bean not found"));
        //When
        try {
            etlService.load(mapping.getName(), params, rows, outs, etlLog.getId());
        } finally {
            //Verify
            verify(mappingDao, times(1)).findByNameAndSource(mapping.getName(), mapping.getSource());
            verify(configService, times(2)).getServices();
            verify(util, times(1)).parseStringToMap(configService.getServices());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfMappingIsNullWhenLoadIsCalled()
            throws ETLException, MappingNotFoundException {
        //Given
        List<Map<String, Object>> rows = new ArrayList<>();
        List<Map<String, Object>> outs = new ArrayList<>();
        mapping.setName(null);
        //When
        etlService.load(mapping.getName(), params, rows, outs, etlLog.getId());
    }

    @Test
    public void shouldDoETL() {
        //Given
        given(etlLogDao.create(etlLog)).willReturn(etlLog);
        given(mappingDao.findByNameAndSource(mapping.getName(), mapping.getSource())).willReturn(dbMapping);
        given(configService.getDataSource(ETLTestHelper.MAPPING_SOURCE)).willReturn(dataSource);
        given(util.getNamedParameterJdbcTemplate(dataSource, ETLTestHelper.DEFAULT_FETCH_SIZE))
                .willReturn(namedParameterJdbcTemplate);

        //When
        etlService.doETL(mapping.getName(), params);

        //Verify
        verify(mappingDao, times(2)).findByNameAndSource(mapping.getName(), mapping.getSource());
        verify(configService, times(1)).getDataSource(ETLTestHelper.MAPPING_SOURCE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfMappingIsNullWhenDoETLIsCalled() throws ETLException {
        //Given
        mapping.setName(null);
        //When
        etlService.doETL(mapping.getName(), params);
    }

    @Test
    public void shouldCreateLogsWhenDoETLIsInvoked() {
        //Given
        given(mappingDao.findByNameAndSource(mapping.getName(), mapping.getSource())).willReturn(dbMapping);
        given(configService.getDataSource(ETLTestHelper.MAPPING_SOURCE)).willReturn(dataSource);
        given(util.getNamedParameterJdbcTemplate(dataSource, ETLTestHelper.DEFAULT_FETCH_SIZE))
                .willReturn(namedParameterJdbcTemplate);
        given(etlLogDao.create(etlLog)).willReturn(etlLog);

        ArgumentCaptor<ETLLog> logArgumentCaptor = ArgumentCaptor.forClass(ETLLog.class);

        //When
        etlService.doETL(mapping.getName(), params);

        //Verify
        verify(mappingDao, times(2)).findByNameAndSource(mapping.getName(), mapping.getSource());
        verify(configService, times(1)).getDataSource(ETLTestHelper.MAPPING_SOURCE);
        verify(etlLogDao, times(1)).create(logArgumentCaptor.capture());
        verify(etlLogDao, times(1)).update(etlLog);
        assertThat(logArgumentCaptor.getValue().getMapping(), equalTo(mapping.getName()));
        assertThat(logArgumentCaptor.getValue().getDatabaseName(), equalTo(mapping.getSource()));
        assertThat(logArgumentCaptor.getValue().getExtractStartTime(), equalTo(dateTime));
        assertThat(logArgumentCaptor.getValue().getExtractEndTime(), equalTo(dateTime));
        assertThat(logArgumentCaptor.getValue().getTransformStartTime(), equalTo(dateTime));
        assertThat(logArgumentCaptor.getValue().getExtractedRecords(), equalTo(0));
        assertThat(logArgumentCaptor.getValue().getTransformedRecords(), equalTo(0));
    }

    private Date createDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar.getTime();
    }
}

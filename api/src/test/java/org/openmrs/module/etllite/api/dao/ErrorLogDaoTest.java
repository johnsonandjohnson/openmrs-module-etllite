/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.dao;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.etllite.api.domain.ErrorLog;
import org.openmrs.module.etllite.api.util.DateUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ErrorLogDaoTest extends BaseModuleContextSensitiveTest {

    private static final String DB = "db";
    private static final String DB_2 = "db2";
    private static final String DB_3 = "db3";
    private static final String MAPPING = "mapping";
    private static final String SOURCE_KEY = "sourceKey";
    private static final String SOURCE_KEY2 = "sourceKey2";
    private static final String SOURCE_VALUE = "sourceValue";
    private static final String SOURCE_VALUE2 = "sourceValue2";

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.XXX";

    //Fri Apr 24 2020 10:45:34
    private static final Date APRIL24 = new Date(1587725134741L);
    private static final Date APRIL25 = DateUtil.plusDays(APRIL24, 1);
    private static final Date APRIL26 = DateUtil.plusDays(APRIL24, 2);

    @Autowired
    private ErrorLogDao errorLogDao;

    @Before
    public void setUp() {
        createErrorLog(DB, SOURCE_KEY, SOURCE_VALUE, APRIL24);
        createErrorLog(DB_2, SOURCE_KEY, SOURCE_VALUE, APRIL24);
        createErrorLog(DB, SOURCE_KEY, SOURCE_VALUE, APRIL25);
        createErrorLog(DB, SOURCE_KEY2, SOURCE_VALUE2, APRIL25);
        createErrorLog(DB, SOURCE_KEY, SOURCE_VALUE, APRIL26);
    }

    @Test
    public void shouldFindBySourceKeyAndRunDate() {
        ErrorLog errorLog =  errorLogDao.findBySourceKeyAndRunDate(DB, MAPPING, SOURCE_KEY, SOURCE_VALUE, APRIL25);

        assertNotNull(errorLog);
        assertThat(DateUtil.dateToString(errorLog.getRunOn(), DATE_TIME_FORMAT, DateUtil.getLocalTimeZone()),
                equalTo(DateUtil.dateToString(APRIL25, DATE_TIME_FORMAT, DateUtil.getLocalTimeZone())));
    }

    @Test
    public void shouldFindByMappingAndRunDate() {
        List<ErrorLog> errorLogs =  errorLogDao.findByMappingAndRunDate(DB, MAPPING, APRIL25);

        assertThat(errorLogs.size(), equalTo(2));
        for (ErrorLog errorLog : errorLogs) {
            assertThat(DateUtil.dateToString(errorLog.getRunOn(), DATE_TIME_FORMAT, DateUtil.getLocalTimeZone()),
                    equalTo(DateUtil.dateToString(APRIL25, DATE_TIME_FORMAT, DateUtil.getLocalTimeZone())));
        }
    }

    @Test
    public void shouldProperlyCreateErrorLog() {
        ErrorLog errorLog = createErrorLog(DB_3, SOURCE_KEY, SOURCE_VALUE, APRIL26);

        assertNotNull(errorLog);
    }

    private ErrorLog createErrorLog(String db, String sourceKey, String sourceValue, Date runOn) {
        ErrorLog errorLog = new ErrorLog();
        errorLog.setDatabaseName(db);
        errorLog.setMapping(ErrorLogDaoTest.MAPPING);
        errorLog.setSourceKey(sourceKey);
        errorLog.setSourceValue(sourceValue);
        errorLog.setRunOn(runOn);

        return errorLogDao.saveOrUpdate(errorLog);
    }
}

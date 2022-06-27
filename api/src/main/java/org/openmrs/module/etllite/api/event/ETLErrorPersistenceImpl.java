/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.event;

import org.openmrs.api.context.Context;
import org.openmrs.module.etllite.api.constants.Constants;
import org.openmrs.module.etllite.api.dao.ETLLogDao;
import org.openmrs.module.etllite.api.dao.ErrorLogDao;
import org.openmrs.module.etllite.api.domain.ETLLog;
import org.openmrs.module.etllite.api.domain.ErrorLog;
import org.openmrs.module.etllite.api.util.DateUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

public class ETLErrorPersistenceImpl implements ETLErrorPersistence {

    private final Object lock = new Object();

    private ErrorLogDao errorLogDao;
    private ETLLogDao etlLogDao;

    @Transactional
    public void persistError(Map<String, Object> properties) {

        Integer jobId = (Integer) properties.get(Constants.ETL_IMPORT_JOB_ID);
        String errorMessage = (String) properties.get(Constants.ETL_IMPORT_FAILURE_MESSAGE);
        String stackTrace = (String) properties.get(Constants.ETL_IMPORT_FAILURE_STACKTRACE);
        String database = (String) properties.get(Constants.ETL_IMPORT_DATABASE);
        String mapping = (String) properties.get(Constants.ETL_IMPORT_MAPPING);
        String sourceKey = (String) properties.get(Constants.ETL_IMPORT_SOURCE_KEY);
        String sourceValue = (String) properties.get(Constants.ETL_IMPORT_SOURCE_VALUE);


        synchronized (lock) {
            ErrorLog existingRecord = getErrorLogDao().findBySourceKeyAndRunDate(
                    database, mapping, sourceKey, sourceValue, DateUtil.getDateWithLocalTimeZone(new Date()));

            if (null == existingRecord) {
                ErrorLog errorLog = new ErrorLog();
                errorLog.setDatabaseName(database);
                errorLog.setMapping(mapping);
                errorLog.setSourceKey(sourceKey);
                errorLog.setSourceValue(sourceValue);
                errorLog.setErrorMessage(errorMessage);
                errorLog.setRunOn(DateUtil.getDateWithLocalTimeZone(new Date()));
                errorLog.setStackTrace(stackTrace);
                getErrorLogDao().create(errorLog);

                // update etl log table with job status and update load records count
                ETLLog etlLog = getEtlLogDao().findById(jobId);
                if (null != etlLog) {
                    etlLog.setJobStatus(false);
                    etlLog.setLoadRecords(0 != etlLog.getLoadRecords() ?
                            etlLog.getLoadRecords() - 1 : etlLog.getLoadRecords());
                    getEtlLogDao().update(etlLog);
                }
            }
        }
    }

    private ErrorLogDao getErrorLogDao() {
        if (errorLogDao == null) {
            errorLogDao = Context.getRegisteredComponent("etllite.ErrorLogDao", ErrorLogDao.class);
        }
        return errorLogDao;
    }

    private ETLLogDao getEtlLogDao() {
        if (etlLogDao == null) {
            etlLogDao = Context.getRegisteredComponent("etllite.LogDao", ETLLogDao.class);
        }
        return etlLogDao;
    }
}

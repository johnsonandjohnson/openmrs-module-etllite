/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.service.impl;

import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.etllite.api.dao.ErrorLogDao;
import org.openmrs.module.etllite.api.domain.ErrorLog;
import org.openmrs.module.etllite.api.domain.VisitErrorLog;
import org.openmrs.module.etllite.api.service.ETLErrorService;
import org.openmrs.module.etllite.api.util.ETLUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ETLErrorServiceImpl extends BaseOpenmrsService implements ETLErrorService {

    private static final String SOURCE_VALUE_SEPARATOR = "~";

    private ErrorLogDao errorLogDao;

    @Override
    public List<VisitErrorLog> getVisitErrorLogs(String databaseName, String mappingName,
                                                 Date startDate, Date endDate) {
        ETLUtil etlUtil = new ETLUtil();
        List<ErrorLog> logs = getErrorLogDao()
            .findByMappingAndBetweenRunDates(databaseName, mappingName, startDate, endDate);
        List<VisitErrorLog> result = new ArrayList<>();
        for (ErrorLog errorLog : logs) {
            String[] parts = errorLog.getSourceValue().split(SOURCE_VALUE_SEPARATOR);
            List<Visit> visitsByPatient = Context.getVisitService().getVisitsByPatient(
                Context.getPatientService().getPatientByUuid(parts[0]));
            boolean alreadyExists = false;
            for (Visit visit : visitsByPatient) {
                if (visit.getVisitType().getName().toLowerCase().equals(parts[1].toLowerCase())
                    && visit.getStartDatetime().getTime() == etlUtil.stringToDate(parts[2], "dd-MMM-yy").getTime()) {
                    alreadyExists = true;
                    break;
                }
            }
            if (!alreadyExists) {
                result.add(new VisitErrorLog(parts[0], parts[1], parts[2]));
            }
        }
        return result;
    }

    private ErrorLogDao getErrorLogDao() {
        if (errorLogDao == null) {
            errorLogDao = Context.getRegisteredComponent("etllite.ErrorLogDao", ErrorLogDao.class);
        }
        return errorLogDao;
    }
}

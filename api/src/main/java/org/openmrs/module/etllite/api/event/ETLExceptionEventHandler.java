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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.util.ContextAware;
import org.openmrs.module.etllite.api.constants.Constants;
import org.openmrs.module.etllite.api.service.ETLEventService;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles exceptions thrown from transform and load velocity templates while invoking ETL util
 * methods or Spring services. Also create events for failed records
 *
 * @author nanakapa
 */
public class ETLExceptionEventHandler implements MethodExceptionEventHandler, ContextAware {

    private static final String SOURCE_KEY = "sourceKey";

    private static final String SOURCE_VALUE = "sourceValue";

    private Context etlContext;

    private static final Log LOGGER = LogFactory.getLog(ETLExceptionEventHandler.class);

    private ETLEventService etlEventService;

    /**
     * This method will be triggered whenever an exception is thrown from the transform or load
     * velocity templates
     *
     * @param clazz  the class from which the exception was thrown
     * @param method the method from which the exception was thrown
     * @param e      the exception object
     * @return null when an exception is thrown so that the velocity template can handle
     */
    @SuppressWarnings("rawtypes")
    public Object methodException(Class clazz, String method, Exception e) {
        LOGGER.error(String.format("Class : %s, method : %s with exception %s . Please check the logs", clazz, method, e));
        final String meta = "Class : " + clazz + " Method : " + method;
        createETLFailureEvent(e, meta);
        return null;
    }

    @Override
    public void setContext(Context context) {
        etlContext = context;
    }

    public void setEtlEventService(ETLEventService etlEventService) {
        this.etlEventService = etlEventService;
    }

    /**
     * Create events for failed records so that failed events will be processed and inserted into
     * error log table
     *
     * @param e
     * @param message
     */
    private void createETLFailureEvent(Exception e, String message) {
        Map<String, Object> params = new HashMap<>();

        params.put(Constants.ETL_IMPORT_FAILURE_MESSAGE, message);
        params.put(Constants.ETL_IMPORT_FAILURE_STACKTRACE, ExceptionUtils.getStackTrace(e));
        params.put(Constants.ETL_IMPORT_JOB_ID, etlContext.get(Constants.ETL_IMPORT_JOB_ID));

        if (etlContext.containsKey(Constants.PARAM_DATABASE)) {
            params.put(Constants.ETL_IMPORT_DATABASE, etlContext.get(Constants.PARAM_DATABASE));
        }
        if (etlContext.containsKey(Constants.PARAM_MAPPING)) {
            params.put(Constants.ETL_IMPORT_MAPPING, etlContext.get(Constants.PARAM_MAPPING));
        }
        if (etlContext.containsKey(SOURCE_KEY)) {
            params.put(Constants.ETL_IMPORT_SOURCE_KEY, etlContext.get(SOURCE_KEY));
        }
        if (etlContext.containsKey(SOURCE_VALUE)) {
            params.put(Constants.ETL_IMPORT_SOURCE_VALUE, etlContext.get(SOURCE_VALUE));
        }
        etlEventService.sendEventMessage(new ETLEvent(Constants.ETL_FAILURE_SUBJECT, params));
    }
}

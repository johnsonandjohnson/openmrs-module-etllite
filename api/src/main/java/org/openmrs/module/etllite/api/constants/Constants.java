/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.constants;

/**
 * Constants for the ETL Module
 *
 * @author nanakapa
 */
public final class Constants {
    public static final String PROJECT_ID = "etllite";

    public static final String ETL_EVENT_IMPORT_DATA = "importData";

    public static final String CONFIG_FILE_NAME = "etllite-configs.json";

    public static final String CONFIG_DIR = "cfl_config";

    public static final String CONFIG_FILE_PATH = CONFIG_DIR + "/" + CONFIG_FILE_NAME;

    public static final String PARAM_SOURCE = "source";

    public static final String PARAM_MAPPINGS = "mappings";

    public static final String SUBJECT_SEPARATOR = "-";

    public static final String CONTEXT_PARAM_UTIL = "util";

    public static final String CONTEXT_PARAM_ROWS = "rows";

    public static final String CONTEXT_PARAM_PARAMS = "params";

    public static final String CONTEXT_PARAM_OUTS = "outs";

    public static final String SUBJECT_RUNNER = "ETLRunnerJob";

    public static final String PARAM_JOB_ID = "JobID";

    public static final String PARAM_MAPPING = "mapping";

    public static final String PARAM_DATABASE = "database";

    public static final String ETL_IMPORT_FAILURE_MESSAGE = "etl-import.failure_message";

    public static final String ETL_IMPORT_FAILURE_STACKTRACE = "etl-import.failure_stacktrace";

    public static final String ETL_IMPORT_DATABASE = "etl-import.database";

    public static final String ETL_IMPORT_MAPPING = "etl-import.mapping";

    public static final String ETL_IMPORT_SOURCE_KEY = "etl-import.sourceKey";

    public static final String ETL_IMPORT_SOURCE_VALUE = "etl-import.sourceValue";

    public static final String ETL_FAILURE_SUBJECT = "etl-failure";

    public static final String ETL_IMPORT_JOB_ID = "etl-import-jobId";

    public static final String CRON_PROPERTY = "cron";

    public static final String INVALID_CONFIG_NAME_MESSAGE = "Name cant't be blank";

    public static final String INVALID_CONFIG_USER_MESSAGE = "User cant't be blank";

    public static final String INVALID_CONFIG_TYPE_MESSAGE = "Type cant't be blank";

    public static final String INVALID_CONFIG_URL_MESSAGE = "Url cant't be blank";

    public static final String INVALID_CONFIG_DB_PASSWORD_MESSAGE = "Password cant't be blank for new config";

    public static final String INVALID_ENUM_TYPE_MESSAGE = "Type isn't valid enum type: MYSQL | MSSQL | POSTGRESQL";

    public static final String CONFIG_SERVICE_INITIALIZATION_ERROR = "config service initialization error";

    /** The default length (size in bytes) of TEXT datatype in MySQL. */
    public static final int MYSQL_TEXT_DATATYPE_LENGTH = 65535;

    private Constants() {
        // private. So can't be initialized
    }
}

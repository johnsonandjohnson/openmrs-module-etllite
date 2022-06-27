/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

// Messages displayed on the frontend

export const FIELD_REQUIRED = 'This field is required';
export const REQUIRED_LABEL = 'obligatory';

// Generic messages
export const GENERIC_FAILURE = 'An error occurred.';
export const INVALID_CONFIGURATION = "Configuration is invalid";
export const GENERIC_PROCESSING = 'Processing...';

// Mapping
export const MAPPING_NAME_DESC = 'The Mapping Name.';
export const MAPPING_QUERY_DESC = 'This is the SQL query to be run at the source. This forms the Extract section of the ETL module. The output of this query is saved under the variable rows and passed to the transform template for transformation. This field is used to test the mapping.';
export const MAPPING_TRANSFORM_DESC = 'The Transformation template. Uses Velocity Syntax. This field is used to test the mapping.';
export const MAPPING_SOURCE_DESC = '';
export const MAPPING_CRON_DESC = 'Optional CRON expression used to run this mapping regularly on a scheduled basis.';
export const MAPPING_FETCH_SIZE_DESC = 'This is the fetch size parameter to retrieve the data in chunks.';
export const MAPPING_LOAD_DESC = 'The Load template. Uses Velocity Syntax. Has access to both the extracted data and transformed data. Typically uses Spring services to load data into this OpenMRS installation.';
export const MAPPING_TEST_RESULTS_SIZE_DESC = 'Number of test results to be displayed.';
export const MAPPING_TEST_BUTTON_TOOLTIP_ENABLED = 'Only Extract and Transform can be tested.';
export const MAPPING_TEST_BUTTON_TOOLTIP_DISABLED = 'The entity must be saved before testing.';
export const MAPPING_NAME_LABEL = 'Name';
export const MAPPING_SOURCE_LABEL = 'Source';
export const MAPPING_FETCH_SIZE_LABEL = 'Fetch Size';
export const MAPPING_QUERY_LABEL = 'Query (Extract)';
export const MAPPING_TRANSFORM_LABEL = 'Transform';
export const MAPPING_LOAD_LABEL = 'Load';
export const MAPPING_CRON_LABEL = 'CRON';
export const MAPPING_TEST_RESULTS_SIZE_LABEL = 'Test Results Size';
export const MAPPING_TEST_RESULTS_EXTRACTED_LABEL = 'Extracted';
export const MAPPING_TEST_RESULTS_TRANSFORMED_LABEL = 'Transformed';

// Mapping response
export const MAPPING_CREATE_SUCCESS = 'Mapping created.';
export const MAPPING_CREATE_FAILURE = 'Mapping creation failure.';
export const MAPPING_UPDATE_SUCCESS = 'Mapping updated.';
export const MAPPING_UPDATE_FAILURE = 'Mapping updated failure.';
export const MAPPING_DELETE_SUCCESS = 'Mapping deleted.';
export const MAPPING_DELETE_FAILURE = 'Mapping deletion failure.';
export const MAPPING_TEST_SUCCESS = 'Mapping tested.';
export const MAPPING_TEST_FAILURE = 'Mapping testing failure.';
export const MAPPING_TEST_PARSE_FAILURE = 'Unable to display mapping testing result.'

// Breadcrumbs
export const MAPPINGS_BREADCRUMB = 'Mappings';
export const SETTINGS_BREADCRUMB = 'Settings';
export const SYSTEM_ADMINISTRATION_BREADCRUMB = 'System Administration';
export const GENERAL_MODULE_BREADCRUMB = 'ETL Lite';
export const ETL_MAPPING_TITLE = "ETL Lite Mappings"

export const ETL_MAPPING_NAME_PREFIX = "Mapping:";
export const ETL_MAPPING_NOT_SAVED = "not saved";
export const ETL_DELETE_MAPPING_TITLE = "Delete Mapping";
export const ETL_DELETE_MAPPING_DESCRIPTION = "Are you sure you want to delete this Mapping?";
export const ETL_DELETE_DATABASE_TITLE = "Delete Database";
export const ETL_DELETE_DATABASE_DESCRIPTION = "Are you sure you want to delete this Database?";

export const ETL_MODAL_DEFAULT_TITLE = "Confirm";
export const ETL_MODAL_DEFAULT_DESCRIPTION = "Are you sure?";
export const ETL_MODAL_CONFIRM_LABEL = "YES";
export const ETL_MODAL_CANCEL_LABEL = "NO";

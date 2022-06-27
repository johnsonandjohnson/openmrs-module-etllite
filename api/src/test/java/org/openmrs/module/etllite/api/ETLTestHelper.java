/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api;

import org.openmrs.module.etllite.api.constants.Constants;
import org.openmrs.module.etllite.api.contract.ConfigRequestWrapper;
import org.openmrs.module.etllite.api.contract.ConfigResponse;
import org.openmrs.module.etllite.api.contract.ConfigResponseWrapper;
import org.openmrs.module.etllite.api.contract.MappingRequest;
import org.openmrs.module.etllite.api.contract.MappingResponse;
import org.openmrs.module.etllite.api.domain.Config;
import org.openmrs.module.etllite.api.domain.Mapping;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * ETLTestHelper Class
 *
 * @author nanakapa
 */
public final class ETLTestHelper {

    public static final int DEFAULT_FETCH_SIZE = 1000;

    public static final String APPLICATION_JSON_UTF8 = "application/json; charset=UTF-8";

    public static final String H2_TEST_DB_URL = "jdbc:h2:mem:openmrs;DB_CLOSE_DELAY=30;LOCK_TIMEOUT=10000";

    public static final String H2_TEST_DB_USER = "sa";

    public static final String H2_TEST_DB_PASSWORD = "Password";

    //Database constants
    public static final String ETL_DB_NAME = "ICEA";

    public static final String ETL_DB_NAME2 = "ICEA2";

    public static final String ETL_DB_NAME3 = "ICEA3";

    public static final String ETL_DB_NAME4 = "ICEA4";

    public static final String ETL_DB_NAME5 = "ICEA5";

    public static final String ETL_DB_NAME6 = "ICEA6";

    public static final String ETL_DB_NAME7 = "ICEA7";

    public static final String ETL_DB_USER = "root";

    public static final String ETL_DB_PWD = "root";

    public static final String ETL_DB_TYPE = "MYSQL";

    public static final String ETL_DB_URL = "jdbc:mysql://localhost:3306/icea";

    public static final String ETL_TEST_QUERY = "select 0;";

    public static final String CONFIG_FILE_NAME = "etllite-configs.json";

    public static final String SERVICES = "patientSrvc:personDAO";

    public static final String NON_EXISTING_DB = "SomeConfigWhichDoesNotExists";

    public static final String BEAN_SERVICE_KEY = "patientSrvc";

    public static final String BEAN_SERVICE_NAME = "personDAO";

    //Mapping constants
    public static final Integer NON_EXISTING_MAPPING_ID = 99;

    public static final Integer MAPPING_ID = 1;

    public static final String MAPPING_NAME = "Patient Data";

    public static final String MAPPING_SOURCE = "ICEA";

    // In this mapping, we map data from location entity to person entity. It does not reflect real life use case, however
    // it tests the following issues :
    // transform location_id integer field to person_uuid string
    // transform date_created date field to person_voidReason string field - usage of formatDate
    // transform name string field to person_gender string field - usage of if-else to set gender value
    public static final String MAPPING_QUERY = "select location_id, name, date_created from location where location_id = :locationId";

    public static final String MAPPING_BATCH_QUERY = "select location_id, name, date_created from location where location_id = 1";

    public static final String MAPPING_LOAD_TEMPLATE = "#if ($outs.size() > 0)\n" + "  #foreach( $row in $outs )\n" + "\t#set($person = $util.newObject(\"org.openmrs.Person\"));\t\n" + "\t#set($person.gender=$row.get(\"gender\"));\t\n" + "\t#set($person.personVoidReason=$row.get(\"dateOfBirth\"));\n" + "\t#set($person.uuid=$row.get(\"externalId\"));\n" + "\t$patientSrvc.savePerson($person)\n" + "  #end\n" + "#end";

    public static final String MAPPING_TRANSFORM_TEMPLATE = "#foreach( $row in $rows )            \n" + "  #set($out = $util.newMap());\n" + "  #if ($row.get(\"name\") == \"Male\")\n" + "\t$out.put(\"gender\", \"male\");\n" + "  #else\n" + "    $out.put(\"gender\", \"female\");\n" + "  #end\n" + "  #set($dateOfBirth = $util.formatDate($row.get(\"date_created\"),\"MM/dd/yyyy\"));\n" + "  $out.put(\"externalId\",$row.get(\"location_id\").toString());\n" + "  $out.put(\"dateOfBirth\",$dateOfBirth);\n" + "  $outs.add($out);\n" + "#end\n";

    public static final String MAPPING_CRON = "0 0 12 1/1 * ? *";

    public static final String MAPPING_CRON_WITHOUT_YEARS = "0 0 12 1/1 * ?";

    public static final String MAPPING_LAB_DATA = "Lab Data";

    public static final String MAPPING_CHECKUP_DATA = "Checkup Data";

    public static final String ETL_NOT_BLANK_STRING = "ETL_NOT_BLANK_STRING";

    private ETLTestHelper() {

    }

    public static Config setUpETLDatabase1() {
        Config database1 = new Config();
        database1.setName(ETLTestHelper.ETL_DB_NAME);
        database1.setType(ETLTestHelper.ETL_DB_TYPE);
        database1.setUser(ETLTestHelper.ETL_DB_USER);
        database1.setDbPassword(ETLTestHelper.ETL_DB_PWD);
        database1.setQuery(ETLTestHelper.ETL_TEST_QUERY);
        database1.setUrl(ETLTestHelper.ETL_DB_URL);

        return database1;
    }

    public static Config setUpETLDatabase2() {
        Config database2 = new Config();
        database2.setName(ETLTestHelper.ETL_DB_NAME2);
        database2.setType(ETLTestHelper.ETL_DB_TYPE);
        database2.setUser(ETLTestHelper.ETL_DB_USER);
        database2.setDbPassword(ETLTestHelper.ETL_DB_PWD);
        database2.setQuery(ETLTestHelper.ETL_TEST_QUERY);
        database2.setUrl(ETLTestHelper.ETL_DB_URL);

        return database2;
    }

    public static Config setUpETLDatabase3() {
        Config database3 = new Config();
        database3.setName(ETLTestHelper.ETL_DB_NAME3);
        database3.setType(ETLTestHelper.ETL_DB_TYPE);
        database3.setUser(ETLTestHelper.ETL_DB_USER);
        database3.setDbPassword(ETLTestHelper.ETL_DB_PWD);
        database3.setQuery(ETLTestHelper.ETL_TEST_QUERY);
        database3.setUrl(ETLTestHelper.ETL_DB_URL);

        return database3;
    }

    public static Config setUpConfigWithAllAttributesNull() {
        Config database4 = new Config();
        database4.setName(null);
        database4.setType(null);
        database4.setUser(null);
        database4.setDbPassword(null);
        database4.setQuery(null);
        database4.setUrl(null);

        return database4;
    }

    public static Config setUpConfigWithBlankPassword() {
        Config database5 = new Config();
        database5.setName(ETLTestHelper.ETL_DB_NAME5);
        database5.setType(ETLTestHelper.ETL_DB_TYPE);
        database5.setUser(ETLTestHelper.ETL_DB_USER);
        database5.setDbPassword(null);
        database5.setQuery(ETLTestHelper.ETL_TEST_QUERY);
        database5.setUrl(ETLTestHelper.ETL_DB_URL);

        return database5;
    }

    public static Config setUpConfigWithAllAttributesBlank() {
        Config database6 = new Config();
        database6.setName(" ");
        database6.setType(" ");
        database6.setUser(" ");
        database6.setDbPassword(" ");
        database6.setQuery(" ");
        database6.setUrl(" ");

        return database6;
    }

    public static Config setUpConfigWithInvalidEnumTyp() {
        Config database7 = new Config();
        database7.setName(ETLTestHelper.ETL_DB_NAME7);
        database7.setType(ETL_NOT_BLANK_STRING);
        database7.setUser(ETLTestHelper.ETL_DB_USER);
        database7.setDbPassword(ETLTestHelper.ETL_DB_PWD);
        database7.setQuery(ETLTestHelper.ETL_TEST_QUERY);
        database7.setUrl(ETLTestHelper.ETL_DB_URL);

        return database7;
    }

    public static ConfigRequestWrapper setUpETLConfigRequest() {
        List<Config> databases = new ArrayList<>();
        ConfigRequestWrapper configRequestWrapper = new ConfigRequestWrapper();
        databases.add(setUpETLDatabase1());
        databases.add(setUpETLDatabase2());
        configRequestWrapper.setDatabases(databases);
        configRequestWrapper.setServices(ETLTestHelper.SERVICES);
        return configRequestWrapper;
    }

    public static ConfigResponse setUpConfigResponse() {
        ConfigResponse configResponse = new ConfigResponse();
        configResponse.setName(ETLTestHelper.ETL_DB_NAME);
        configResponse.setType(ETLTestHelper.ETL_DB_TYPE);
        configResponse.setUser(ETLTestHelper.ETL_DB_USER);
        configResponse.setQuery(ETLTestHelper.ETL_TEST_QUERY);
        configResponse.setUrl(ETLTestHelper.ETL_DB_URL);
        return configResponse;
    }

    public static ConfigResponseWrapper setUpETLConfigResponse() {
        List<ConfigResponse> databases = new ArrayList<>();
        databases.add(setUpConfigResponse());
        ConfigResponseWrapper configResponseWrapper = new ConfigResponseWrapper();
        configResponseWrapper.setDatabases(databases);
        configResponseWrapper.setServices(ETLTestHelper.SERVICES);
        return configResponseWrapper;
    }

    public static Mapping setUpNewMapping() {
        Mapping mapping = new Mapping();
        mapping.setName(MAPPING_NAME);
        mapping.setSource(MAPPING_SOURCE);
        mapping.setQuery(MAPPING_QUERY);
        mapping.setLoadTemplate(MAPPING_LOAD_TEMPLATE);
        mapping.setTransformTemplate(MAPPING_TRANSFORM_TEMPLATE);
        mapping.setCronExpression(MAPPING_CRON);
        mapping.setFetchSize(ETLTestHelper.DEFAULT_FETCH_SIZE);
        return mapping;
    }

    public static Mapping setUpTestMapping() {
        Mapping mapping = new Mapping();
        mapping.setName(MAPPING_NAME);
        mapping.setSource(MAPPING_SOURCE);
        mapping.setQuery(MAPPING_BATCH_QUERY);
        mapping.setLoadTemplate(MAPPING_LOAD_TEMPLATE);
        mapping.setTransformTemplate(MAPPING_TRANSFORM_TEMPLATE);
        mapping.setCronExpression(MAPPING_CRON);
        return mapping;
    }

    public static Mapping setUpDbMapping() {
        Mapping mapping = setUpNewMapping();
        mapping.setId(MAPPING_ID);
        mapping.setFetchSize(DEFAULT_FETCH_SIZE);
        return mapping;
    }

    public static Mapping setUpLabDataMapping() {
        Mapping mapping = new Mapping();
        mapping.setName(MAPPING_LAB_DATA);
        mapping.setSource(MAPPING_SOURCE);
        mapping.setQuery(MAPPING_QUERY);
        mapping.setLoadTemplate(MAPPING_LOAD_TEMPLATE);
        mapping.setTransformTemplate(MAPPING_TRANSFORM_TEMPLATE);
        mapping.setCronExpression(MAPPING_CRON);
        return mapping;
    }

    public static Mapping setUpCheckupDataMapping() {
        Mapping mapping = new Mapping();
        mapping.setName(MAPPING_CHECKUP_DATA);
        mapping.setSource(MAPPING_SOURCE);
        mapping.setQuery(MAPPING_QUERY);
        mapping.setLoadTemplate(MAPPING_LOAD_TEMPLATE);
        mapping.setTransformTemplate(MAPPING_TRANSFORM_TEMPLATE);
        mapping.setCronExpression(MAPPING_CRON);
        return mapping;
    }

    public static void checkMappingAsserts(Mapping expectedMapping, Mapping actualMapping) {
        assertThat(actualMapping.getName(), equalTo(expectedMapping.getName()));
        assertThat(actualMapping.getSource(), equalTo(expectedMapping.getSource()));
        assertThat(actualMapping.getQuery(), equalTo(expectedMapping.getQuery()));
        assertThat(actualMapping.getLoadTemplate(), equalTo(expectedMapping.getLoadTemplate()));
        assertThat(actualMapping.getTransformTemplate(), equalTo(expectedMapping.getTransformTemplate()));
        assertThat(actualMapping.getCronExpression(), equalTo(expectedMapping.getCronExpression()));
    }

    public static MappingRequest setUpMappingRequest() {
        MappingRequest mappingRequest = new MappingRequest();
        mappingRequest.setName(MAPPING_NAME);
        mappingRequest.setSource(MAPPING_SOURCE);
        mappingRequest.setQuery(MAPPING_QUERY);
        mappingRequest.setLoadTemplate(MAPPING_LOAD_TEMPLATE);
        mappingRequest.setTransformTemplate(MAPPING_TRANSFORM_TEMPLATE);
        mappingRequest.setCronExpression(MAPPING_CRON);
        return mappingRequest;
    }

    public static MappingResponse setUpMappingResponse() {
        MappingResponse mappingResponse = new MappingResponse();
        mappingResponse.setName(MAPPING_NAME);
        mappingResponse.setSource(MAPPING_SOURCE);
        mappingResponse.setQuery(MAPPING_QUERY);
        mappingResponse.setLoadTemplate(MAPPING_LOAD_TEMPLATE);
        mappingResponse.setTransformTemplate(MAPPING_TRANSFORM_TEMPLATE);
        mappingResponse.setCronExpression(MAPPING_CRON);
        return mappingResponse;
    }

    public static String getSubjectForSingleMapping(String source, String mapping) {
        return source + Constants.SUBJECT_SEPARATOR + mapping;
    }
}

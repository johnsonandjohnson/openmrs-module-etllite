/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.context.Context;
import org.openmrs.module.etllite.api.BaseTest;
import org.openmrs.module.etllite.api.domain.Mapping;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

/**
 * ETL Util Test Class
 *
 * @author nanakapa
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class ETLUtilTest extends BaseTest {

    @InjectMocks
    private ETLUtil etlUtil = new ETLUtil();

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(ETLUtilTest.class);
        PowerMockito.mockStatic(Context.class);
    }

    public static final String PARAM_RETRY_ATTEMPTS = "retryAttempts";
    public static final String PARAM_PHONE = "phone";
    public static final String PARAM_ACTOR_ID = "actorId";
    public static final String PARAM_ACTOR_TYPE = "actorType";
    public static final String PARAM_EXTERNAL_ID = "externalId";
    public static final String PARAM_EXTERNAL_TYPE = "externalType";
    public static final String PARAM_PLAYED_MESSAGES = "playedMessages";
    public static final String PARAM_REF_KEY = "refKey";
    public static final String PARAM_FLOW_NAME = "flowName";
    public static final String PARAM_PARAMS = "params";
    public static final String PARAM_CONFIG = "config";
    private static final String FLOW_NAME = "flowName";
    private static final String PHONE = "543-654-531";
    private static final String CONFIG = "config";
    private static final String ACTOR_ID = "actor_id";
    private static final String ACTOR_TYPE = "actor_type";
    private static final String EXTERNAL_ID = "external_id";
    private static final String EXTERNAL_TYPE = "external_type";
    private static final String PLAYED_MESSAGES = "played messages";
    private static final String REF_KEY = "refKey";
    private static final int RETRY_ATTEMPTS = 2;
    private static final String CUSTOM_PARAMS_DELIMITER = ",";
    private static final String CUSTOM_PARAMS_KEY_VALUE_SEPARATOR = "=";
    private static final String PARAMS_AS_STRING = "externalType=external_type,actorType=actor_type,actorId=actor_id,"
            + "playedMessages=played messages,phone=543-654-531,externalId=external_id,refKey=refKey,retryAttempts=2";

    @Test
    public void test1() {

        Map<String, Object> propertiesObject = new HashMap<String, Object>() {{
            put(PARAM_CONFIG, CONFIG);
            put(PARAM_FLOW_NAME, FLOW_NAME);
            put(PARAM_PARAMS, new HashMap<String, Object>() {{
                put(PARAM_PHONE, PHONE);
                put(PARAM_ACTOR_ID, ACTOR_ID);
                put(PARAM_ACTOR_TYPE, ACTOR_TYPE);
                put(PARAM_EXTERNAL_ID, EXTERNAL_ID);
                put(PARAM_EXTERNAL_TYPE, EXTERNAL_TYPE);
                put(PARAM_PLAYED_MESSAGES, PLAYED_MESSAGES);
                put(PARAM_REF_KEY, REF_KEY);
                put(PARAM_RETRY_ATTEMPTS, RETRY_ATTEMPTS);
            }});
        }};

        Map<String, String> propertiesString = new HashMap<String, String>() {{
            put(PARAM_CONFIG, CONFIG);
            put(PARAM_FLOW_NAME, FLOW_NAME);
            put(PARAM_PARAMS, PARAMS_AS_STRING);
        }};


        Map<String, String> actual = new HashMap<>();
        for (String key : propertiesObject.keySet()) {
            if (PARAM_PARAMS.equals(key)) {
                actual.put(key, Joiner.on(CUSTOM_PARAMS_DELIMITER)
                        .withKeyValueSeparator(CUSTOM_PARAMS_KEY_VALUE_SEPARATOR)
                        .join((Map<?, ?>) propertiesObject.get(key)));
            } else {
                actual.put(key, (String) propertiesObject.get(key));
            }
        }

        assertEquals(actual.size(), propertiesObject.size());
        assertTrue(actual.get(PARAM_PARAMS) instanceof String);
        assertEquals(actual.get(PARAM_PARAMS), PARAMS_AS_STRING);
        assertEquals(actual.get(PARAM_CONFIG), CONFIG);
        assertEquals(actual.get(PARAM_FLOW_NAME), FLOW_NAME);


        Map<String, Object> actual2 = new HashMap<>();
        for (String key : propertiesString.keySet()) {
            if (PARAM_PARAMS.equals(key)) {
                Map<String, Object> params = new HashMap<String, Object>(Splitter.on(CUSTOM_PARAMS_DELIMITER)
                        .withKeyValueSeparator(CUSTOM_PARAMS_KEY_VALUE_SEPARATOR)
                        .split(propertiesString.get(key)));
                if (params.containsKey(PARAM_RETRY_ATTEMPTS)) {
                    params.put(PARAM_RETRY_ATTEMPTS, Integer.valueOf(
                            (String) params.get(PARAM_RETRY_ATTEMPTS)));
                }
                actual2.put(key, params);
            } else {
                actual2.put(key, propertiesString.get(key));
            }
        }

        assertEquals(actual.size(), propertiesObject.size());
    }

    @Test
    public void shouldReturnNewObjectInstanceOfTheSpecifiedClassWhenNewObjectMethodIsCalled() throws ParseException,
            IllegalAccessException, InstantiationException, ClassNotFoundException {
        //Given
        Class testClass = Mapping.class;

        given(Context.loadClass(testClass.getName())).willReturn(testClass);

        Object classObj = etlUtil.newObject(testClass.getName());
        //When & Then
        assertNotNull(classObj);
    }

    @Test
    public void shouldReturnNullIfTheClassIsNotFoundWhenNewObjectIsCalled() throws ParseException,
            IllegalAccessException, InstantiationException, ClassNotFoundException {
        //Given
        given(Context.loadClass("someclass")).willReturn(null);
        //When
        Object classObj = etlUtil.newObject("someclass");

        //Then
        assertNull(classObj);
    }

    @Test
    public void shouldReturnNullForClassNotFoundWhenNewObjectIsCalled() throws ParseException, IllegalAccessException,
            InstantiationException, ClassNotFoundException {
        //Given
        given(Context.loadClass("someclass")).willThrow(new ClassNotFoundException());
        //When
        Object classObj = etlUtil.newObject("someclass");

        //Then
        assertNull(classObj);
    }

    @Test
    public void shouldFormatSQLDateObjectToTheSpecifiedFormat() throws ParseException {
        //Given
        // Convert string to date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String testDateStr = "18-09-2015 11:35:42";
        Date utilDate = dateFormat.parse(testDateStr);
        //When
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        String strDate = etlUtil.formatDate(sqlDate, "MM/dd/yyyy");
        //Then
        assertNotNull(strDate);
        assertThat(strDate, is("09/18/2015"));
    }

    @Test
    public void shouldFormatUtilDateObjectToTheSpecifiedFormat() throws ParseException {
        //Given
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String testDateStr = "18-09-2015 11:35:42";
        Date utilDate = dateFormat.parse(testDateStr);
        //When
        String strDate = etlUtil.formatDate(utilDate, "MM/dd/yyyy");
        //Then
        assertNotNull(strDate);
        assertThat(strDate, is("09/18/2015"));
    }

    @Test
    public void shouldCreateNewHashMap() {
        Map<String, Object> map = etlUtil.newMap();
        assertNotNull(map);
    }

    @Test
    public void shouldConvertToLong() {
        Long longValue = etlUtil.toLong("10");
        assertNotNull(longValue);
        assertThat(longValue.getClass().getName(), is("java.lang.Long"));
        assertThat(longValue, is(10L));
    }

    @Test
    public void shouldCreateNewHashSet() {
        Set<Object> set = etlUtil.newSet();
        assertNotNull(set);
    }

    @Test
    public void shouldConvertDateFormatInStringToDate() {
        //Given
        String dateInStringFormat = "2015-12-05";

        //When
        Date date = etlUtil.stringToDate(dateInStringFormat, "yyyy-MM-dd");

        //Then
        assertNotNull(date);
        Calendar resultCalendar = Calendar.getInstance();
        resultCalendar.setTime(date);
        assertThat(resultCalendar.get(Calendar.YEAR), is(2015));
        assertThat(resultCalendar.get(Calendar.MONTH), is(Calendar.DECEMBER));
        assertThat(resultCalendar.get(Calendar.DAY_OF_WEEK), is(Calendar.SATURDAY));
    }

}

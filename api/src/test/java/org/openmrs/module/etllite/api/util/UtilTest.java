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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.etllite.api.BaseTest;
import org.openmrs.module.etllite.api.ETLTestHelper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Util Test Class
 *
 * @author nanakapa
 */
@RunWith(MockitoJUnitRunner.class)
public class UtilTest extends BaseTest {

    @Mock
    private DriverManagerDataSource dataSource;

    @InjectMocks
    private Util util = new Util();

    @Test
    public void shouldParseStringToMap() {
        //Given
        Map<String, String> map = util.parseStringToMap(ETLTestHelper.SERVICES);
        //When & Then
        assertNotNull(map);
        assertThat(map.size(), is(1));
        assertTrue(map.containsKey(ETLTestHelper.BEAN_SERVICE_KEY));
        assertThat(map.get(ETLTestHelper.BEAN_SERVICE_KEY), is(ETLTestHelper.BEAN_SERVICE_NAME));
    }

    @Test
    public void shouldReturnEmptyMapIfTheStringIsEmptyOrNull() {
        //Given & When
        Map<String, String> map = util.parseStringToMap(null);
        //Then
        assertNotNull(map);
        assertThat(map.size(), is(0));
    }

    @Test
    public void shouldReturnIllegalArgumentIfTheStringFormatIsNotCorrect() {
        expectedException.expect(IllegalArgumentException.class);
        //Given & When
        Map<String, String> map = util.parseStringToMap(ETLTestHelper.BEAN_SERVICE_NAME);
    }

    @Test
    public void shouldThrowIllegalArgumentIfTheDataSourceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        //Given & When
        NamedParameterJdbcTemplate template = util.getNamedParameterJdbcTemplate(null, 0);
    }

    @Test
    public void shouldReturnNamedParameterJDBCTemplateIfTheDataSourceIsNotNull() {
        //Given & When
        NamedParameterJdbcTemplate template = util.getNamedParameterJdbcTemplate(dataSource, 0);
        //Then
        assertNotNull(template);
    }
}

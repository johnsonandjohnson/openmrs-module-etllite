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
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DateUtil.class, TimeZone.class})
public class DateUtilTest {

    @Test
    public void shouldSuccessfullyParseDateTime() {
        Date expected = createDate(2010, Calendar.NOVEMBER, 16, 15, 43, 59, "Asia/Almaty");
        Date actual = DateUtil.parse("2010-11-16T15:43:59.000+06:00");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldSuccessfullyAddDaysToDate() {
        Date expected = createDate(2010, Calendar.MARCH, 2, 15, 43, 59, "CET");
        Date date = createDate(2010, Calendar.FEBRUARY, 27, 15, 43, 59, "CET");
        Date actual = DateUtil.plusDays(date, 3);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldSuccessfullyConvertDateToUtcTimeZone() {
        Date expectedUtc = createDate(2010, Calendar.NOVEMBER, 16, 14, 43, 59, "UTC");
        Date dateCet = createDate(2010, Calendar.NOVEMBER, 16, 15, 43, 59, "CET");
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        PowerMockito.mockStatic(TimeZone.class);
        BDDMockito.given(TimeZone.getTimeZone("UTC")).willReturn(timeZone);
        Date actual = DateUtil.getDateWithDefaultTimeZone(dateCet);
        assertThat(actual, equalTo(expectedUtc));
    }

    @Test
    public void shouldReturnDateWithLocalTimeZone() throws Exception {
        Date date = createDate(2010, Calendar.NOVEMBER, 16, 15, 43, 59, "Asia/Almaty");
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Almaty");
        PowerMockito.mockStatic(TimeZone.class);
        BDDMockito.given(TimeZone.getDefault()).willReturn(timeZone);

        String expected = "2010-11-16T15:43:59.000+06:00";
        String actual = DateUtil.dateToStringLocalTimeZone(date);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void shouldReturnConversionResultAsExpected() {
        String expectedDateAsString = "2012-01-10T00:00:00.000+06:00";
        Date actual = DateUtil.parse(expectedDateAsString);
        assertThat(DateUtil.dateToString(actual, null, TimeZone.getTimeZone("Asia/Almaty")), equalTo(expectedDateAsString));
    }

    @Test
    public void shouldProperlySetTimeOfDay() {
        int hour = 4;
        int minute = 15;
        int seconds = 25;

        Date dateToConvert = createDate(2010, Calendar.NOVEMBER, 16, 15, 43, 59, "UTC");
        Date expectedDate = createDate(2010, Calendar.NOVEMBER, 16, hour, minute, seconds, "UTC");

        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        PowerMockito.mockStatic(TimeZone.class);
        BDDMockito.given(TimeZone.getDefault()).willReturn(timeZone);

        assertThat(DateUtil.setTimeOfDay(dateToConvert, hour, minute, seconds), equalTo(expectedDate));
    }

    private Date createDate(int year, int month, int day, int hour, int minute, int second, String timezone) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeZone(TimeZone.getTimeZone(timezone));
        return calendar.getTime();
    }
}

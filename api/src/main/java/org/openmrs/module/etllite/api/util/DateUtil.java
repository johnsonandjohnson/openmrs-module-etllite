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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class DateUtil {

    private static final Log LOGGER = LogFactory.getLog(DateUtil.class);

    private static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("UTC");

    public static Date parse(String dateTime) {
        return parse(dateTime, null);
    }

    public static Date parse(String dateTime, String pattern) {
        String datePattern = pattern;
        if (StringUtils.isBlank(pattern)) {
            datePattern = ISO_DATE_TIME_FORMAT;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        Date result = null;
        try {
            result = simpleDateFormat.parse(dateTime);
        } catch (ParseException e) {
            LOGGER.error(String.format("Could not parse `%s` date using `%s` pattern", dateTime, datePattern));
        }
        return result;
    }

    public static Date now() {
        return getDateWithDefaultTimeZone(new Date());
    }

    public static Date plusDays(Date date, int duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, duration);
        return calendar.getTime();
    }

    public static Date setTimeOfDay(Date date, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.setTimeZone(getLocalTimeZone());
        return calendar.getTime();
    }

    public static Date getDateWithDefaultTimeZone(Date timestamp) {
        return getDateWithTimeZone(timestamp, DEFAULT_TIME_ZONE);
    }

    public static Date getDateWithLocalTimeZone(Date timestamp) {
        return getDateWithTimeZone(timestamp, getLocalTimeZone());
    }

    public static Date getDateWithTimeZone(Date timestamp, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(timestamp);
        return calendar.getTime();
    }

    public static String dateToString(Date date) {
        return dateToString(date, ISO_DATE_TIME_FORMAT, DEFAULT_TIME_ZONE);
    }

    public static String dateToStringLocalTimeZone(Date date) {
        return dateToString(date, ISO_DATE_TIME_FORMAT, getLocalTimeZone());
    }

    public static String dateToString(Date date, String format, TimeZone timeZone) {
        if (date == null) {
            return null;
        }
        String dateFormat = format;
        if (StringUtils.isBlank(format)) {
            dateFormat = ISO_DATE_TIME_FORMAT;
        }
        TimeZone dateTimeZone = timeZone;
        if (timeZone == null) {
            dateTimeZone = DEFAULT_TIME_ZONE;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setTimeZone(dateTimeZone);

        return simpleDateFormat.format(date);
    }

    public static TimeZone getLocalTimeZone() {
        return TimeZone.getDefault();
    }

    private DateUtil() {
    }
}

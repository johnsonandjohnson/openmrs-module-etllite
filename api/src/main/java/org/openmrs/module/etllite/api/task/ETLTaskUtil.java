/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.task;

import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;

public final class ETLTaskUtil {

    public static Date nextDate(String cron) {
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cron);
        return cronSequenceGenerator.next(new Date());
    }

    public static String generateTaskName(String subject, String jobId) {
        return String.format("%s-%s", subject, jobId);
    }

    public static String removeYearsFromCronIfNeeded(String cron) {
        String[] fields = StringUtils.tokenizeToStringArray(cron, " ");
        String parsedCron;
        if (fields.length > 6) {
            parsedCron = StringUtils.arrayToDelimitedString(Arrays.copyOf(fields, fields.length - 1), " ");
        } else {
            parsedCron = cron;
        }

        return parsedCron;
    }

    private ETLTaskUtil() {
    }
}

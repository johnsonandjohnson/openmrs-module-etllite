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

import org.junit.Test;
import org.openmrs.module.etllite.api.ETLTestHelper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ETLTaskUtilTest {

    @Test
    public void shouldProperlyGenerateTaskName() {
        assertThat(ETLTaskUtil.generateTaskName("subject", "jobId"), equalTo("subject-jobId"));
    }

    @Test
    public void shouldProperlyParseCronExpIfLength6() {
        assertThat(ETLTaskUtil.removeYearsFromCronIfNeeded(
                ETLTestHelper.MAPPING_CRON_WITHOUT_YEARS), equalTo(ETLTestHelper.MAPPING_CRON_WITHOUT_YEARS));
    }

    @Test
    public void shouldProperlyParseCronExpIfLength7() {
        assertThat(ETLTaskUtil.removeYearsFromCronIfNeeded(
                ETLTestHelper.MAPPING_CRON), equalTo(ETLTestHelper.MAPPING_CRON_WITHOUT_YEARS));
    }
}

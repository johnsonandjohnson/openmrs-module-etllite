/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.etllite.api.event.ETLEvent;
import org.openmrs.module.etllite.api.task.AbstractCronTask;

public interface ETLSchedulerService extends OpenmrsService {

    void safeScheduleJob(ETLEvent event, String cronExp, AbstractCronTask cronTask);

    void safeUnscheduleJob(String subject, String jobId);
}

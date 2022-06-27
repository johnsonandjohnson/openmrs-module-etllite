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

import org.openmrs.api.context.Context;
import org.openmrs.module.etllite.api.constants.Constants;
import org.openmrs.module.etllite.api.exception.ETLRuntimeException;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.tasks.AbstractTask;

public abstract class AbstractCronTask extends AbstractTask {

    @Override
    public void execute() {
        try {
            executeTask();
        } finally {
            rescheduleTask();
        }
    }

    protected abstract void executeTask();

    private void rescheduleTask() {
        try {
            getTaskDefinition().setStartTime(ETLTaskUtil.nextDate(getTaskDefinition().getProperty(Constants.CRON_PROPERTY)));
            Context.getSchedulerService().scheduleTask(getTaskDefinition());
        } catch (SchedulerException ex) {
            throw new ETLRuntimeException(ex);
        }
    }
}

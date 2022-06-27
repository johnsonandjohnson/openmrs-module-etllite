/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.etllite.api.constants.Constants;
import org.openmrs.module.etllite.api.event.ETLEvent;
import org.openmrs.module.etllite.api.exception.ETLRuntimeException;
import org.openmrs.module.etllite.api.service.ETLSchedulerService;
import org.openmrs.module.etllite.api.task.AbstractCronTask;
import org.openmrs.module.etllite.api.task.ETLTaskUtil;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.Map;

public class ETLSchedulerServiceImpl extends BaseOpenmrsService implements ETLSchedulerService {

    private static final Log LOGGER = LogFactory.getLog(ETLSchedulerServiceImpl.class);

    private SchedulerService schedulerService;

    @Override
    public void safeScheduleJob(ETLEvent event, String cronExp, AbstractCronTask cronTask) {
        String taskName = event.generateTaskName();
        shutdownTask(taskName);

        String parsedCronExp = ETLTaskUtil.removeYearsFromCronIfNeeded(cronExp);

        TaskDefinition taskDefinition = new TaskDefinition();
        taskDefinition.setName(taskName);
        taskDefinition.setTaskClass(cronTask.getClass().getName());
        taskDefinition.setStartTime(ETLTaskUtil.nextDate(parsedCronExp));
        taskDefinition.setStartOnStartup(Boolean.TRUE);
        taskDefinition.setProperties(prepareProperties(event, parsedCronExp));
        taskDefinition.setRepeatInterval(0L);

        try {
            schedulerService.saveTaskDefinition(taskDefinition);
            schedulerService.scheduleTask(taskDefinition);
        } catch (SchedulerException ex) {
            throw new ETLRuntimeException(ex);
        }
    }

    @Override
    public void safeUnscheduleJob(String subject, String jobId) {
        shutdownTask(ETLTaskUtil.generateTaskName(subject, jobId));
    }

    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    private void shutdownTask(String taskName) {
        try {
            TaskDefinition taskDefinition = schedulerService.getTaskByName(taskName);
            if (taskDefinition != null) {
                schedulerService.shutdownTask(taskDefinition);
                schedulerService.deleteTask(taskDefinition.getId());
            }
        } catch (SchedulerException ex) {
            LOGGER.error(ex);
        }
    }

    private Map<String, String> prepareProperties(ETLEvent etlEvent, String cron) {
        Map<String, String> result = etlEvent.convertProperties();
        result.put(Constants.CRON_PROPERTY, cron);

        return result;
    }
}

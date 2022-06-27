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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.etllite.api.ETLTestHelper;
import org.openmrs.module.etllite.api.constants.Constants;
import org.openmrs.module.etllite.api.event.ETLEvent;
import org.openmrs.module.etllite.api.exception.ETLRuntimeException;
import org.openmrs.module.etllite.api.service.impl.ETLSchedulerServiceImpl;
import org.openmrs.module.etllite.api.task.ETLJobTask;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ETLSchedulerServiceTest {

    private static final String TEST_SUBJECT = "subject";
    private static final String JOB_ID_VALUE = "2";

    @Mock
    private SchedulerService schedulerService;

    @InjectMocks
    private ETLSchedulerServiceImpl etlSchedulerService = new ETLSchedulerServiceImpl();

    private ArgumentCaptor<TaskDefinition> taskDefinitionCaptor;
    private ETLEvent etlEvent;

    @Before
    public void setUp() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_JOB_ID, JOB_ID_VALUE);
        etlEvent = new ETLEvent(TEST_SUBJECT, parameters);

        taskDefinitionCaptor = ArgumentCaptor.forClass(TaskDefinition.class);
    }

    @Test
    public void shouldProperlyScheduleTask() throws Exception {
        when(schedulerService.getTaskByName(TEST_SUBJECT + "-" + JOB_ID_VALUE)).thenReturn(new TaskDefinition());

        etlSchedulerService.safeScheduleJob(etlEvent, ETLTestHelper.MAPPING_CRON, new ETLJobTask());

        verify(schedulerService).shutdownTask(any(TaskDefinition.class));
        verify(schedulerService).scheduleTask(taskDefinitionCaptor.capture());

        TaskDefinition captured = taskDefinitionCaptor.getValue();
        assertThat(captured.getName(), equalTo(TEST_SUBJECT + "-" + JOB_ID_VALUE));
        assertThat(captured.getTaskClass(), equalTo(ETLJobTask.class.getName()));
        assertThat(captured.getStartTime(), notNullValue());
        assertThat(captured.getStartOnStartup(), equalTo(true));
        assertThat(captured.getRepeatInterval(), equalTo(0L));

        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.PARAM_JOB_ID, JOB_ID_VALUE);
        properties.put(Constants.CRON_PROPERTY, ETLTestHelper.MAPPING_CRON_WITHOUT_YEARS);
        assertThat(captured.getProperties(), equalTo(properties));
    }

    @Test
    public void shouldScheduleEvenIfUnScheduleFailed() throws Exception {
        when(schedulerService.getTaskByName(TEST_SUBJECT + "-" + JOB_ID_VALUE)).thenReturn(new TaskDefinition());
        doThrow(SchedulerException.class).when(schedulerService).shutdownTask(any(TaskDefinition.class));

        etlSchedulerService.safeScheduleJob(etlEvent, ETLTestHelper.MAPPING_CRON, new ETLJobTask());

        verify(schedulerService).shutdownTask(any(TaskDefinition.class));
        verify(schedulerService).scheduleTask(any(TaskDefinition.class));
    }

    @Test(expected = ETLRuntimeException.class)
    public void shouldThrowExceptionIfScheduleFailed() throws Exception {
        when(schedulerService.getTaskByName(TEST_SUBJECT + "-" + JOB_ID_VALUE)).thenReturn(new TaskDefinition());
        doThrow(SchedulerException.class).when(schedulerService).shutdownTask(any(TaskDefinition.class));
        doThrow(SchedulerException.class).when(schedulerService).scheduleTask(any(TaskDefinition.class));

        etlSchedulerService.safeScheduleJob(etlEvent, ETLTestHelper.MAPPING_CRON, new ETLJobTask());

        verify(schedulerService).shutdownTask(any(TaskDefinition.class));
        verify(schedulerService).scheduleTask(any(TaskDefinition.class));
    }

    @Test
    public void shouldProperlyUnScheduleTask() throws Exception {
        when(schedulerService.getTaskByName(TEST_SUBJECT + "-" + JOB_ID_VALUE)).thenReturn(new TaskDefinition());

        etlSchedulerService.safeUnscheduleJob(TEST_SUBJECT, JOB_ID_VALUE);

        verify(schedulerService).shutdownTask(any(TaskDefinition.class));
    }
}

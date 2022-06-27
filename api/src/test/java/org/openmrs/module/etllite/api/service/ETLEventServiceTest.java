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
import org.mockito.MockitoAnnotations;
import org.openmrs.event.Event;
import org.openmrs.event.EventMessage;
import org.openmrs.module.etllite.api.BaseTest;
import org.openmrs.module.etllite.api.event.ETLEvent;
import org.openmrs.module.etllite.api.service.impl.ETLEventServiceImpl;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Event.class})
public class ETLEventServiceTest extends BaseTest {

    private ETLEventService etlEventService = new ETLEventServiceImpl();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(ETLEventServiceTest.class);
        PowerMockito.mockStatic(Event.class);
    }

    @Test
    public void shouldProperlySendEventMessage() {
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");
        ETLEvent etlEvent = new ETLEvent("subject", params);

        etlEventService.sendEventMessage(etlEvent);

        EventMessage eventMessage = new EventMessage();
        eventMessage.put("key", "value");

        PowerMockito.verifyStatic();
        Event.fireEvent("subject", eventMessage);
    }
}

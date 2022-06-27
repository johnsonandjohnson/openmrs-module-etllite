/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.etllite.api.BaseTest;
import org.openmrs.module.etllite.api.constants.Constants;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class})
public class ETLFailureEventListenerTest extends BaseTest {

    @Mock
    private ETLErrorPersistence etlErrorPersistence;

    @InjectMocks
    private ETLFailureEventListener etlFailureEventListener = new ETLFailureEventListener();

    @Before
    public void setUp() {
        mockStatic(Context.class);

        PowerMockito.when(Context.getRegisteredComponent(anyString(), eq(ETLErrorPersistence.class)))
                .thenReturn(etlErrorPersistence);
    }

    @Test
    public void shouldReturnProperSubject() {
        assertThat(etlFailureEventListener.getSubject(), equalTo(Constants.ETL_FAILURE_SUBJECT));
    }

    @Test
    public void shouldHandleEvent() {
        etlFailureEventListener.handleEvent(new HashMap<>());

        verify(etlErrorPersistence).persistError(anyMapOf(String.class, Object.class));
    }
}

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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.etllite.api.BaseTest;
import org.openmrs.module.etllite.api.constants.Constants;
import org.openmrs.module.etllite.api.service.ETLService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ETLImportDataEventListenerTest extends BaseTest {

    @Mock
    private ETLService etlService;

    @InjectMocks
    private ETLImportDataEventListener etlImportDataEventListener = new ETLImportDataEventListener();

    @Test
    public void shouldReturnProperSubject() {
        assertThat(etlImportDataEventListener.getSubject(), equalTo(Constants.ETL_EVENT_IMPORT_DATA));
    }

    @Test
    public void shouldHandleEvent() {
        List<String> mappings = new ArrayList<>();
        mappings.add("mapping1");
        mappings.add("mapping2");

        Map<String, Object> params = new HashMap<>();
        params.put(Constants.PARAM_MAPPINGS, mappings);

        etlImportDataEventListener.handleEvent(params);

        verify(etlService, times(2)).doETL(anyString(), anyMapOf(String.class, Object.class));
    }

}

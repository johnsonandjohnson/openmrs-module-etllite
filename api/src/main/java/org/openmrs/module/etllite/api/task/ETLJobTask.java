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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.etllite.api.constants.Constants;
import org.openmrs.module.etllite.api.service.ETLService;

import java.util.HashMap;
import java.util.Map;

public class ETLJobTask extends AbstractCronTask {

    private static final Log LOGGER = LogFactory.getLog(ETLJobTask.class);

    @Override
    protected void executeTask() {
        LOGGER.debug("ETL Batch Job started");

        Map<String, Object> properties = new HashMap<>();
        properties.putAll(getTaskDefinition().getProperties());

        String mapping = (String) properties.get(Constants.PARAM_MAPPING);

        LOGGER.debug(String.format("ETL started for mapping : %s", mapping));
        Context.getRegisteredComponent("etllite.etlService", ETLService.class).doETL(mapping, properties);
    }
}

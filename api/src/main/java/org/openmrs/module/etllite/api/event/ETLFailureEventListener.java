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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.etllite.api.constants.Constants;

import java.util.Map;

public class ETLFailureEventListener extends AbstractETLEventListener {

    private static final Log LOGGER = LogFactory.getLog(ETLFailureEventListener.class);

    @Override
    public String getSubject() {
        return Constants.ETL_FAILURE_SUBJECT;
    }

    @Override
    protected void handleEvent(Map<String, Object> properties) {
        LOGGER.debug("ETL Failure handler invoked");
        Context.getRegisteredComponent("etllite.ETLErrorPersistence", ETLErrorPersistence.class)
                .persistError(properties);
    }
}

/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Response Contract for ETL databases and spring services
 *
 * @author nanakapa
 */

public class ConfigResponseWrapper {

    private List<ConfigResponse> databases = new ArrayList<>();

    private String services;

    public List<ConfigResponse> getDatabases() {
        return databases;
    }

    public void setDatabases(List<ConfigResponse> databases) {
        this.databases = databases;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }
}

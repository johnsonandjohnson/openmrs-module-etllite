/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.builder;

import org.openmrs.module.etllite.api.contract.ConfigResponse;
import org.openmrs.module.etllite.api.domain.Config;

/**
 * Response Builder for Database settings
 *
 * @author nanakapa
 */
public class ConfigResponseBuilder {

    /**
     * Config response builder
     *
     * @param config ETL database settings
     * @return <code>ConfigResponse</code>
     */
    public ConfigResponse createFrom(Config config) {
        ConfigResponse configResponse = new ConfigResponse();
        configResponse.setName(config.getName());
        configResponse.setType(config.getType());
        configResponse.setUser(config.getUser());
        configResponse.setQuery(config.getQuery());
        configResponse.setUrl(config.getUrl());
        return configResponse;
    }
}

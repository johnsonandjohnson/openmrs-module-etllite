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

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.etllite.api.domain.Config;
import org.openmrs.module.etllite.api.util.EncryptionUtil;

/**
 * Builder class for Database configurations
 *
 * @author nanakapa
 */
public class ConfigBuilder {

    private EncryptionUtil encryptionUtil;

    /**
     * Encrypts the database password and creates the Config object from Config request object.
     *
     * @param configRequest config object from request
     * @param newConfig     config to be added or updated
     * @return Updated Config object
     */
    public Config createConfig(Config configRequest, Config newConfig) {
        newConfig.setName(configRequest.getName());
        newConfig.setType(configRequest.getType());
        newConfig.setUrl(configRequest.getUrl());
        newConfig.setUser(configRequest.getUser());
        if (StringUtils.isNotEmpty(configRequest.getDbPassword())) {
            newConfig.setDbPassword(encryptionUtil.encryptAsString(configRequest.getDbPassword()));
        }
        newConfig.setQuery(configRequest.getQuery());
        return newConfig;
    }

    /**
     * Decrypts the database password
     *
     * @param dbPassword encrypted database password
     * @return decrypted database password
     */
    public String decryptPassword(String dbPassword) {
        String decryptedPassword = null;

        if (StringUtils.isEmpty(dbPassword)) {
            decryptedPassword = "";
        } else {
            decryptedPassword = encryptionUtil.decryptAsString(dbPassword);
        }
        return decryptedPassword;
    }

    public void setEncryptionUtil(EncryptionUtil encryptionUtil) {
        this.encryptionUtil = encryptionUtil;
    }
}

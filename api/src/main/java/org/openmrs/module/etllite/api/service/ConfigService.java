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

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.etllite.api.contract.ConfigRequestWrapper;
import org.openmrs.module.etllite.api.domain.Config;
import org.openmrs.module.etllite.api.util.PrivilegeConstants;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Create, update, delete and extract ETL database settings using the ConfigService APIs
 * Configuration Service, adapted from IVR Module
 *
 * @author nanakapa
 */
public interface ConfigService extends OpenmrsService {

    /**
     * This method fetches the database settings for the specified ETL database
     *
     * @param name ETL database name
     * @return database settings for the specified ETL database
     */
    @Authorized(PrivilegeConstants.ETL_SETTINGS_PRIVILEGE)
    Config getConfig(String name);

    /**
     * This method fetches all the ETL database settings
     *
     * @return list of ETL database settings
     */
    @Authorized(PrivilegeConstants.ETL_SETTINGS_PRIVILEGE)
    List<Config> allConfigs();

    /**
     * This method checks the specified ETL database exists or not.
     *
     * @param name ETL database name
     * @return true if the specified ETL database is present, false if not present
     */
    @Authorized(PrivilegeConstants.ETL_SETTINGS_PRIVILEGE)
    boolean hasConfig(String name);

    /**
     * This method creates or updates the specified ETL database settings and spring services
     *
     * @param configRequestWrapper contains list of ETL database settings and spring services.
     */
    @Authorized(PrivilegeConstants.ETL_SETTINGS_PRIVILEGE)
    void createOrUpdateConfigs(ConfigRequestWrapper configRequestWrapper);

    /**
     * This method deletes the specified ETL database settings
     *
     * @param name ETL database name
     */
    @Authorized(PrivilegeConstants.ETL_SETTINGS_PRIVILEGE)
    void deleteConfig(String name);

    /**
     * This method deletes all ETL database settings
     */
    @Authorized(PrivilegeConstants.ETL_SETTINGS_PRIVILEGE)
    void deleteAll();

    /**
     * This method fetches the <code>DataSource</code> for the specified ETL database
     *
     * @param name ETL database configuration name
     * @return <code>DataSource</code> for the specified ETL database
     */
    @Authorized(PrivilegeConstants.ETL_SETTINGS_PRIVILEGE)
    DataSource getDataSource(String name);

    /**
     * This method tests the ETL database connection by using the parameters defined while creating
     * the ETL database settings
     *
     * @param name ETL database name
     * @return true if the connection to the database is successful, false if not.
     * @throws SQLException
     */
    @Authorized(PrivilegeConstants.ETL_SETTINGS_PRIVILEGE)
    boolean testDatabase(String name) throws SQLException;

    /**
     * This method fetches the spring services required for ETL DB configuration
     *
     * @return comma separated spring services
     */
    @Authorized(PrivilegeConstants.ETL_SETTINGS_PRIVILEGE)
    String getServices();
}

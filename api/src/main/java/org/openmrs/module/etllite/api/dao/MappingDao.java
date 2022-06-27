/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.dao;

import org.openmrs.api.db.OpenmrsMetadataDAO;
import org.openmrs.module.etllite.api.domain.Mapping;

import java.util.List;

/**
 * The MappingDao class. This is DAO for Mapping entity.
 */
public interface MappingDao extends OpenmrsMetadataDAO<Mapping> {

    /**
     * Fetches ETL Mapping details by name and ETL source
     *
     * @param name   mapping name
     * @param source ETL source
     * @return <code>Mapping</code> by name, null if the specified mapping not found
     */
    Mapping findByNameAndSource(String name, String source);

    /**
     * Fetches ETL Mapping details by source
     *
     * @param source source system to extract the data
     * @return list of mappings by name, null if there is no matching name
     */

    List<Mapping> findBySource(String source);

    Mapping create(Mapping mapping);

    Mapping findById(Integer id);

    Mapping update(Mapping mapping);

    List<Mapping> retrieveAll();

    void deleteAll();
}

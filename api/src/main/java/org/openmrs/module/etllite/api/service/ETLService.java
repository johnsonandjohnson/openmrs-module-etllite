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

import org.openmrs.module.etllite.api.exception.ETLException;
import org.openmrs.module.etllite.api.exception.MappingNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Defines APIs to extract, transform and load data
 *
 * @author nanakapa
 */
public interface ETLService {

    /**
     * Extracts data from the data source associated with the mapping.
     *
     * @param mappingName mapping name
     * @param params      parameters required for ETL
     * @return data extracted using the query defined in the mapping
     * @throws MappingNotFoundException if the mapping does not exists in database
     * @throws IOException if issue with evaluate template
     */
    List<Map<String, Object>> extract(String mappingName, Map<String, Object> params) throws MappingNotFoundException,
            IOException;

    /**
     * Transforms the extracted data using transform velocity template associated with the mapping.
     *
     * @param mappingName mapping name
     * @param params      parameters required for ETL
     * @param rows        data extracted using the extract method
     * @return data transformed data using velocity transform template
     * @throws MappingNotFoundException if the mapping does not exists in database
     */
    List<Map<String, Object>> transform(String mappingName, Map<String, Object> params, List<Map<String, Object>> rows)
            throws ETLException, MappingNotFoundException;

    /**
     * Loads the transformed data using load velocity template
     *
     * @param mappingName mapping name
     * @param params      parameters required for ETL
     * @param rows        extracted data using the query defined in the mapping
     * @param outs        transformed data
     * @param jobId       contains the ETL job id
     * @throws ETLException             if there is any error in the etl process
     * @throws MappingNotFoundException if the mapping does not exists in database
     */
    void load(String mappingName, Map<String, Object> params, List<Map<String, Object>> rows,
              List<Map<String, Object>> outs, Integer jobId) throws ETLException, MappingNotFoundException;

    /**
     * Invokes the complete ETL process and calls extract, transform and load methods in order.
     *
     * @param mappingName mapping name
     * @param params      parameters required for ETL
     */
    void doETL(String mappingName, Map<String, Object> params);
}

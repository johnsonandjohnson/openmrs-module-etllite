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

import org.openmrs.module.etllite.api.contract.MappingResponse;
import org.openmrs.module.etllite.api.domain.Mapping;

/**
 * Response Builder class for Mappings
 *
 * @author nanakapa
 */

public class MappingResponseBuilder {

    /**
     * Creates <code>MappingResponse</code> from mapping entity
     *
     * @param mapping Mapping entity
     * @return <code>MappingResponse</code>
     */
    public MappingResponse createFrom(Mapping mapping) {
        MappingResponse mappingResponse = new MappingResponse();
        mappingResponse.setId(mapping.getId());
        mappingResponse.setName(mapping.getName());
        mappingResponse.setSource(mapping.getSource());
        mappingResponse.setQuery(mapping.getQuery());
        mappingResponse.setTransformTemplate(mapping.getTransformTemplate());
        mappingResponse.setLoadTemplate(mapping.getLoadTemplate());
        mappingResponse.setCronExpression(mapping.getCronExpression());
        mappingResponse.setFetchSize(mapping.getFetchSize());
        mappingResponse.setTestResultsSize(mapping.getTestResultsSize());
        return mappingResponse;
    }
}

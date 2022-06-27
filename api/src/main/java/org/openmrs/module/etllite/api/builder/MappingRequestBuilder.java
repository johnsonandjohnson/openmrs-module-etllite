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

import org.openmrs.module.etllite.api.contract.MappingRequest;
import org.openmrs.module.etllite.api.domain.Mapping;

/**
 * Request Builder class for Mapping
 *
 * @author nanakapa
 */

public class MappingRequestBuilder {

    /**
     * Creates Mapping entity from <code>MappingRequest</code>
     *
     * @param mappingRequest contains details required to create or update a mapping
     * @return <code>Mapping</code> entity
     */
    public Mapping createFrom(MappingRequest mappingRequest) {
        Mapping mapping = new Mapping();
        mapping.setName(mappingRequest.getName());
        mapping.setSource(mappingRequest.getSource());
        mapping.setTransformTemplate(mappingRequest.getTransformTemplate());
        mapping.setLoadTemplate(mappingRequest.getLoadTemplate());
        mapping.setQuery(mappingRequest.getQuery());
        mapping.setCronExpression(mappingRequest.getCronExpression());
        mapping.setFetchSize(mappingRequest.getFetchSize());
        mapping.setTestResultsSize(mappingRequest.getTestResultsSize());
        return mapping;
    }
}

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

/**
 * Mapping Request Class
 *
 * @author nanakapa
 */
public class MappingRequest {

    private String name;

    private String query;

    private String cronExpression;

    private String loadTemplate;

    private String source;

    private String transformTemplate;

    private int fetchSize;

    private int testResultsSize;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getLoadTemplate() {
        return loadTemplate;
    }

    public void setLoadTemplate(String loadTemplate) {
        this.loadTemplate = loadTemplate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTransformTemplate() {
        return transformTemplate;
    }

    public void setTransformTemplate(String transformTemplate) {
        this.transformTemplate = transformTemplate;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public int getTestResultsSize() {
        return testResultsSize;
    }

    public void setTestResultsSize(int testResultsSize) {
        this.testResultsSize = testResultsSize;
    }
}

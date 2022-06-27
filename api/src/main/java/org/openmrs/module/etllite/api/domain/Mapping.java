/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.domain;

import org.openmrs.BaseOpenmrsMetadata;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Objects;

import static org.openmrs.module.etllite.api.constants.Constants.MYSQL_TEXT_DATATYPE_LENGTH;

/**
 * This class encapsulates the ETL Mapping configuration,
 * composed of queries and velocity template required for transformation and load
 *
 * @author nanakapa
 */

@Entity(name = "etl.Mapping")
@Table(name = "etl_mappings",
        uniqueConstraints = @UniqueConstraint(name = "UNIQUE_ETL_MAPPING_IDX", columnNames = {"name", "source"}))
public class Mapping extends BaseOpenmrsMetadata {

    private static final long serialVersionUID = -1473402788029598370L;

    private static final String TEXT = "text";

    @Id
    @GeneratedValue
    @Column(name = "etl_mappings_id")
    private Integer id;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false, columnDefinition = TEXT, length = MYSQL_TEXT_DATATYPE_LENGTH)
    private String query;

    @Column(columnDefinition = TEXT, length = MYSQL_TEXT_DATATYPE_LENGTH)
    private String transformTemplate;

    @Column(columnDefinition = TEXT, length = MYSQL_TEXT_DATATYPE_LENGTH)
    private String loadTemplate;

    @Column
    private String cronExpression;

    @Column(nullable = false)
    private int fetchSize;

    @Column(nullable = false)
    private int testResultsSize;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTransformTemplate() {
        return transformTemplate;
    }

    public void setTransformTemplate(String transformTemplate) {
        this.transformTemplate = transformTemplate;
    }

    public String getLoadTemplate() {
        return loadTemplate;
    }

    public void setLoadTemplate(String loadTemplate) {
        this.loadTemplate = loadTemplate;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Mapping that = (Mapping) o;

        return Objects.equals(this.getName(), that.getName()) && Objects.equals(this.getSource(), that.getSource());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getSource());
    }

    @Override
    public String toString() {
        return "Mapping{" + "name='" + getName() + '\'' + ", source='" + getSource() + '\'' + '}';
    }
}

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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

/**
 * Log domain
 *
 * @author nanakapa
 */

@Entity(name = "etl.Log")
@Table(name = "etl_logs")
public class ETLLog extends AbstractBaseOpenmrsData {

    private static final long serialVersionUID = 6050392062725760469L;

    @Id
    @GeneratedValue
    @Column(name = "etl_logs_id")
    private Integer id;

    @Column
    private String databaseName;

    @Column
    private String mapping;

    @Column
    private Date extractStartTime;

    @Column
    private Date extractEndTime;

    @Column
    private Date transformStartTime;

    @Column
    private Date transformEndTime;

    @Column
    private Date loadStartTime;

    @Column
    private Date loadEndTime;

    @Column(nullable = false)
    private int extractedRecords;

    @Column(nullable = false)
    private int transformedRecords;

    @Column(nullable = false)
    private int loadRecords;

    @Column
    private Date runOn;

    @Column
    private boolean jobStatus;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public Date getExtractStartTime() {
        return extractStartTime;
    }

    public void setExtractStartTime(Date extractStartTime) {
        this.extractStartTime = extractStartTime;
    }

    public Date getExtractEndTime() {
        return extractEndTime;
    }

    public void setExtractEndTime(Date extractEndTime) {
        this.extractEndTime = extractEndTime;
    }

    public Date getTransformStartTime() {
        return transformStartTime;
    }

    public void setTransformStartTime(Date transformStartTime) {
        this.transformStartTime = transformStartTime;
    }

    public Date getTransformEndTime() {
        return transformEndTime;
    }

    public void setTransformEndTime(Date transformEndTime) {
        this.transformEndTime = transformEndTime;
    }

    public Date getLoadStartTime() {
        return loadStartTime;
    }

    public void setLoadStartTime(Date loadStartTime) {
        this.loadStartTime = loadStartTime;
    }

    public Date getLoadEndTime() {
        return loadEndTime;
    }

    public void setLoadEndTime(Date loadEndTime) {
        this.loadEndTime = loadEndTime;
    }

    public int getExtractedRecords() {
        return extractedRecords;
    }

    public void setExtractedRecords(int extractedRecords) {
        this.extractedRecords = extractedRecords;
    }

    public int getTransformedRecords() {
        return transformedRecords;
    }

    public void setTransformedRecords(int transformedRecords) {
        this.transformedRecords = transformedRecords;
    }

    public int getLoadRecords() {
        return loadRecords;
    }

    public void setLoadRecords(int loadRecords) {
        this.loadRecords = loadRecords;
    }

    public Date getRunOn() {
        return runOn;
    }

    public void setRunOn(Date runOn) {
        this.runOn = runOn;
    }

    public boolean getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(boolean jobStatus) {
        this.jobStatus = jobStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ETLLog etlLog = (ETLLog) o;

        return Objects.equals(this.id, etlLog.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Log{" + "id=" + id + '}';
    }
}


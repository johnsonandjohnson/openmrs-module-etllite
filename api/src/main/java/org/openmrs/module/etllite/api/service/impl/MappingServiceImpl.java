/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.etllite.api.constants.Constants;
import org.openmrs.module.etllite.api.dao.MappingDao;
import org.openmrs.module.etllite.api.domain.Mapping;
import org.openmrs.module.etllite.api.event.ETLEvent;
import org.openmrs.module.etllite.api.exception.ETLException;
import org.openmrs.module.etllite.api.exception.MappingAlreadyExistsException;
import org.openmrs.module.etllite.api.exception.MappingNotFoundException;
import org.openmrs.module.etllite.api.service.ETLSchedulerService;
import org.openmrs.module.etllite.api.service.ETLService;
import org.openmrs.module.etllite.api.service.MappingService;
import org.openmrs.module.etllite.api.task.ETLJobTask;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MappingService Implementation Class
 *
 * @author nanakapa
 */
public class MappingServiceImpl extends BaseOpenmrsService implements MappingService {

    private static final String ERROR_MESSAGE = "Mapping does not exist! : ";

    private static final String PARAM_EXTRACTED = "extracted";

    private static final String PARAM_TRANSFORMED = "transformed";

    // This is used to limit the number of extracted and transform results. The browser will crash if the data is huge
    private static final int DEFAULT_RESULTS_SIZE = 10;

    private MappingDao mappingDao;

    private ETLService etlService;

    private ETLSchedulerService schedulerService;

    @Override
    @Transactional(noRollbackFor = MappingAlreadyExistsException.class)
    public Mapping create(Mapping mapping) throws MappingAlreadyExistsException {
        return createInternal(mapping, true);
    }

    private Mapping createInternal(final Mapping mapping, final boolean manageScheduledTask)
            throws MappingAlreadyExistsException {
        // check for duplicates in database
        if (StringUtils.isEmpty(mapping.getName()) || StringUtils.isEmpty(mapping.getSource())) {
            throw new IllegalArgumentException("Mapping name and source are mandatory parameters to create the mapping.");
        }
        if (null != getMappingDao().findByNameAndSource(mapping.getName(), mapping.getSource())) {
            throw new MappingAlreadyExistsException("Mapping already exists! :" + mapping.getName());
        }

        if (manageScheduledTask && !StringUtils.isEmpty(mapping.getCronExpression())) {
            scheduleCron(mapping.getSource(), mapping.getName(), mapping.getCronExpression());
        }

        return getMappingDao().create(mapping);
    }

    @Override
    @Transactional(noRollbackFor = {MappingAlreadyExistsException.class, MappingNotFoundException.class})
    public Mapping saveMapping(final Mapping mapping) throws MappingAlreadyExistsException, MappingNotFoundException {
        final Mapping existingMapping = mapping.getId() != null ? getMappingDao().findById(mapping.getId()) : null;

        final Mapping savedMapping;

        if (existingMapping == null) {
            savedMapping = createInternal(mapping, false);
        } else {
            savedMapping = updateInternal(mapping, false);
        }

        return savedMapping;
    }

    @Override
    @Transactional(noRollbackFor = MappingNotFoundException.class)
    public Mapping update(Mapping mapping) throws MappingNotFoundException {
        return updateInternal(mapping, true);
    }

    private Mapping updateInternal(final Mapping mapping, final boolean manageScheduledTask)
            throws MappingNotFoundException {
        Mapping existingMapping = getMappingDao().findById(mapping.getId());

        if (null == existingMapping) {
            throw new MappingNotFoundException(ERROR_MESSAGE + mapping.getName());
        }
        existingMapping.setName(mapping.getName());
        existingMapping.setSource(mapping.getSource());
        existingMapping.setQuery(mapping.getQuery());
        existingMapping.setTransformTemplate(mapping.getTransformTemplate());
        existingMapping.setLoadTemplate(mapping.getLoadTemplate());
        existingMapping.setCronExpression(mapping.getCronExpression());
        existingMapping.setFetchSize(mapping.getFetchSize());
        existingMapping.setTestResultsSize(mapping.getTestResultsSize());

        if (manageScheduledTask) {
            if (!StringUtils.isEmpty(mapping.getCronExpression())) {
                rescheduleCron(existingMapping.getSource(), existingMapping.getName(), existingMapping.getCronExpression());
            } else {
                unScheduleCron(existingMapping.getSource(), existingMapping.getName());
            }
        }

        return getMappingDao().update(existingMapping);
    }

    @Override
    @Transactional(noRollbackFor = MappingNotFoundException.class)
    public void delete(Integer id) throws MappingNotFoundException {
        Mapping existingMapping = getMappingDao().findById(id);

        if (null == existingMapping) {
            throw new MappingNotFoundException(ERROR_MESSAGE + id);
        }
        getMappingDao().delete(existingMapping);
        if (!StringUtils.isEmpty(existingMapping.getCronExpression())) {
            unScheduleCron(existingMapping.getSource(), existingMapping.getName());
        }
    }

    @Override
    public Mapping findById(Integer id) {
        return getMappingDao().findById(id);
    }

    @Override
    public List<Mapping> findBySource(String source) {
        return getMappingDao().findBySource(source);
    }

    @Override
    public List<Mapping> findAll() {
        return getMappingDao().retrieveAll();
    }

    @Override
    public Map<String, List<Map<String, Object>>> testMapping(Integer id)
            throws ETLException, MappingNotFoundException, IOException {
        Map<String, List<Map<String, Object>>> testData = new HashMap<>();
        Map<String, Object> params = new HashMap<>();

        Mapping mapping = getMappingDao().findById(id);

        if (null == mapping) {
            throw new MappingNotFoundException(ERROR_MESSAGE + id);
        }
        int resultsSize = mapping.getTestResultsSize() != 0 ? mapping.getTestResultsSize() : DEFAULT_RESULTS_SIZE;
        params.put(Constants.PARAM_SOURCE, mapping.getSource());
        List<Map<String, Object>> extractedData = getEtlService().extract(mapping.getName(), params);
        if (!extractedData.isEmpty() && extractedData.size() > resultsSize) {
            extractedData = extractedData.subList(0, resultsSize);
        }
        List<Map<String, Object>> transformedData = getEtlService().transform(mapping.getName(), params, extractedData);
        if (!transformedData.isEmpty() && transformedData.size() > resultsSize) {
            transformedData = transformedData.subList(0, resultsSize);
        }
        testData.put(PARAM_EXTRACTED, extractedData);
        testData.put(PARAM_TRANSFORMED, transformedData);
        return testData;
    }

    private void scheduleCron(String source, String mapping, String cronExp) {
        Map<String, Object> params = new HashMap<>();

        params.put(Constants.PARAM_MAPPING, mapping);
        params.put(Constants.PARAM_SOURCE, source);
        params.put(Constants.PARAM_JOB_ID, generateJobId(source, mapping));

        getSchedulerService().safeScheduleJob(new ETLEvent(Constants.SUBJECT_RUNNER, params), cronExp, new ETLJobTask());
    }

    private String generateJobId(String source, String mapping) {
        return new StringBuilder().append(source).append("-").append(mapping).toString();
    }

    private void unScheduleCron(String source, String mapping) {
        getSchedulerService().safeUnscheduleJob(Constants.SUBJECT_RUNNER, generateJobId(source, mapping));
    }

    private void rescheduleCron(String source, String mapping, String cron) {
        unScheduleCron(source, mapping);
        scheduleCron(source, mapping, cron);
    }

    private MappingDao getMappingDao() {
        if (mappingDao == null) {
            mappingDao = Context.getRegisteredComponent("etllite.MappingDao", MappingDao.class);
        }
        return mappingDao;
    }

    private ETLService getEtlService() {
        if (etlService == null) {
            etlService = Context.getRegisteredComponent("etllite.etlService", ETLService.class);
        }
        return etlService;
    }

    private ETLSchedulerService getSchedulerService() {
        if (schedulerService == null) {
            schedulerService = Context.getRegisteredComponent("etl.schedulerService", ETLSchedulerService.class);
        }
        return schedulerService;
    }
}

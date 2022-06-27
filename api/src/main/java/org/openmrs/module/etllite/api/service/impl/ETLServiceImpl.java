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

import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.openmrs.api.APIException;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.etllite.api.constants.Constants;
import org.openmrs.module.etllite.api.dao.ETLLogDao;
import org.openmrs.module.etllite.api.dao.MappingDao;
import org.openmrs.module.etllite.api.domain.ETLLog;
import org.openmrs.module.etllite.api.domain.Mapping;
import org.openmrs.module.etllite.api.event.ETLExceptionEventHandler;
import org.openmrs.module.etllite.api.exception.ETLException;
import org.openmrs.module.etllite.api.exception.MappingNotFoundException;
import org.openmrs.module.etllite.api.service.ConfigService;
import org.openmrs.module.etllite.api.service.ETLService;
import org.openmrs.module.etllite.api.util.DateUtil;
import org.openmrs.module.etllite.api.util.ETLUtil;
import org.openmrs.module.etllite.api.util.Util;
import org.springframework.beans.BeansException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * ETLService Implementation
 *
 * @author nanakapa
 */
public class ETLServiceImpl extends ETLExceptionEventHandler implements ETLService {

    private static final Log LOGGER = LogFactory.getLog(ETLServiceImpl.class);

    private static final String ROOT_LOGGER = "root";

    private static final String MAPPING_ERROR = "mapping name or source can not be null";

    private static final String MAPPING_NOT_FOUND = "Mapping does not exist! : ";

    private static final String ETL_EXTRACT = "ETL Extract";

    private static final String ETL_TRANSFORM = "ETL Transform";

    private static final String ETL_LOAD = "ETL Load";

    private MappingDao mappingDao;

    private ConfigService configService;

    private ETLUtil etlUtil;

    private Util util;

    private ETLLogDao etlLogDao;

    private Map<String, String> beansToLoad = new HashMap<>();

    public void initialize() {
        try {
            // The default Velocity.init creates a velocity.log file for logging
            // in the current directory of where the web server was started from
            // This might cause permission issues in some cases like a automated tool trying to restart the server
            // So we reset this to use the existing logger rather than writing into velocity.log
            Properties props = new Properties();
            props.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, Log4JLogChute.class.getName());
            props.setProperty(Log4JLogChute.RUNTIME_LOG_LOG4J_LOGGER, ROOT_LOGGER);
            Velocity.init(props);
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Error initializing template engine: %s", e.toString()), e);
        }
    }

    @Override
    @Transactional(noRollbackFor = {MappingNotFoundException.class, IOException.class})
    public List<Map<String, Object>> extract(String mappingName, Map<String, Object> params)
            throws MappingNotFoundException, IOException {

        Mapping mapping = findMapping(mappingName, params);

        VelocityContext context = new VelocityContext();
        context.put(Constants.CONTEXT_PARAM_PARAMS, params);
        context.put("lastRunDate", getLastRunDate(mapping.getSource(), mapping.getName()));
        loadUtilsInContext(context);
        String query = evalTemplate(mapping.getQuery(), context, ETL_EXTRACT);

        LOGGER.debug(String.format("[E] started, mapping : %s", mappingName));

        DataSource dataSource = configService.getDataSource(mapping.getSource());

        NamedParameterJdbcTemplate template = util.getNamedParameterJdbcTemplate(dataSource, mapping.getFetchSize());

        SqlParameterSource namedParameters = new MapSqlParameterSource(params);
        List<Map<String, Object>> rows = template.queryForList(query, namedParameters);

        LOGGER.debug(String.format("[E] completed, mapping : %s, extracted rows: %d ", mappingName, rows.size()));

        return rows;
    }

    @Override
    @Transactional(noRollbackFor = {MappingNotFoundException.class, ETLException.class})
    public List<Map<String, Object>> transform(String mappingName, Map<String, Object> params,
                                               List<Map<String, Object>> rows)
            throws ETLException, MappingNotFoundException {

        LOGGER.debug(String.format("[T] started, mapping : %s", mappingName));

        Mapping mapping = findMapping(mappingName, params);

        // Output of transformed rows will be saved here
        List<Map<String, Object>> transformedRows = new ArrayList<>();

        try {
            VelocityContext context = buildContext(rows, params, transformedRows);

            evalTemplate(mapping.getTransformTemplate(), context, ETL_TRANSFORM);
        } catch (IOException e) {
            String message = String.format("ETL Transform error, mapping = %s", mappingName);
            throw new ETLException(message, e);
        }
        LOGGER.debug(String.format("[T] completed, mapping : %s, transformed: %d", mapping, transformedRows.size()));
        return transformedRows;
    }

    @Override
    @Transactional(noRollbackFor = {MappingNotFoundException.class, ETLException.class})
    public void load(String mappingName, Map<String, Object> params, List<Map<String, Object>> rows,
                     List<Map<String, Object>> outs, Integer jobId) throws ETLException, MappingNotFoundException {

        Mapping mapping = findMapping(mappingName, params);
        LOGGER.debug(String.format("[L] started, mapping : %s", mappingName));

        try {
            VelocityContext context = buildContext(rows, params, outs);
            context.put(Constants.PARAM_DATABASE, mapping.getSource());
            context.put(Constants.PARAM_MAPPING, mapping.getName());
            context.put(Constants.ETL_IMPORT_JOB_ID, jobId);

            // includes spring services additionally in the velocity context
            loadBeans(context, beansToLoad);

            evalTemplate(mapping.getLoadTemplate(), context, ETL_LOAD);
        } catch (IOException e) {
            String message = String.format("ETL Load error, mapping = %s", mappingName);
            throw new ETLException(message, e);
        }
        LOGGER.debug(String.format("[L] completed, mapping : %s", mappingName));
    }

    @Override
    @Transactional
    public void doETL(String mappingName, Map<String, Object> params) {
        Mapping mapping = null;
        try {
            mapping = findMapping(mappingName, params);
            LOGGER.info(String.format("[ETL] started, source : %s, mapping : %s", mapping.getSource(), mappingName));
            int fetchSize = mapping.getFetchSize();

            ETLLog etlLog = new ETLLog();
            etlLog.setDatabaseName(mapping.getSource());
            etlLog.setMapping(mappingName);
            etlLog.setRunOn(DateUtil.getDateWithLocalTimeZone(new Date()));
            etlLog.setExtractStartTime(DateUtil.now());
            //Extract and fetch the rows based on the query
            List<Map<String, Object>> rows = extract(mappingName, params);
            etlLog.setExtractedRecords(rows.size());
            etlLog.setExtractEndTime(DateUtil.now());

            etlLog.setTransformStartTime(DateUtil.now());
            etlLog.setLoadStartTime(DateUtil.now());
            //Set the number of load records to the number of extracted data and in case of error reduce the load count
            etlLog.setLoadRecords(rows.size());
            //set the job status to success in case of any failures update the job status
            etlLog.setJobStatus(true);
            ETLLog dbETLLog = etlLogDao.create(etlLog);

            // Process the rows in batches and the batch size is the fetch size value defined in the UI
            for (List<Map<String, Object>> subRows : Lists.partition(rows, fetchSize)) {
                LOGGER.info(String.format("Processing records with batch size : = %d", subRows.size()));

                //Transform the extracted rows based on the transform velocity template
                List<Map<String, Object>> transformedRows = transform(mappingName, params, subRows);
                dbETLLog.setTransformedRecords(rows.size());
                dbETLLog.setTransformEndTime(DateUtil.now());
                //Load the transformed data using spring services into the target system
                load(mappingName, params, subRows, transformedRows, dbETLLog.getId());
            }
            dbETLLog.setLoadEndTime(DateUtil.now());
            etlLogDao.update(etlLog);

        } catch (MappingNotFoundException e) {
            LOGGER.error(String.format("ETL mapping does not exist, source = %s, mapping = %s", params.get(Constants.PARAM_SOURCE),
                    mapping), e);
        } catch (ETLException e) {
            LOGGER.error(String.format("ETL error executing, source = %s, mapping = %s", mapping.getSource(), mapping), e);
        } catch (IOException e) {
            LOGGER.error(String.format("ETL error in fetching extract query, source = %s, mapping = %s",
                    mapping.getSource(), mapping), e);
        }
        LOGGER.info(String.format("[ETL] completed, source : %s, mapping : %s", params.get(Constants.PARAM_SOURCE), mappingName));
    }

    public void setMappingDao(MappingDao mappingDao) {
        this.mappingDao = mappingDao;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setEtlUtil(ETLUtil etlUtil) {
        this.etlUtil = etlUtil;
    }

    public void setUtil(Util util) {
        this.util = util;
    }

    public void setEtlLogDao(ETLLogDao etlLogDao) {
        this.etlLogDao = etlLogDao;
    }

    /**
     * loads the spring beans and includes in the velocity context
     *
     * @param context     <code>VelocityContext</code>
     * @param beansToLoad list of spring beans to load
     */
    private void loadBeans(VelocityContext context, Map<String, String> beansToLoad) {
        loadServices();
        StringBuilder notFoundServices = new StringBuilder();

        for (Map.Entry<String, String> entry : beansToLoad.entrySet()) {
            String serviceKey = entry.getValue();

            // We allow both loading OpenMRS service by providing their class (required for services such a
            // the idgen service) or by their bean name.
            Object service = tryLoadingService(serviceKey);
            if (service == null) {
                service = tryLoadingBean(serviceKey);
            }

            if (service == null) {
                notFoundServices.append(entry.getValue());
                notFoundServices.append('\n');
            } else {
                context.put(entry.getKey(), service);
            }
        }
        // We couldn't find some services
        if (!notFoundServices.toString().isEmpty()) {
            throw new IllegalArgumentException(String.format("Didn't load some services %s", notFoundServices));
        }
    }

    private Object tryLoadingService(String serviceKey) {
        Object service = null;
        try {
            Class<?> serviceClass = Thread.currentThread().getContextClassLoader().loadClass(serviceKey);
            service = ServiceContext.getInstance().getService(serviceClass);
        } catch (ClassNotFoundException | APIException ex) {
            LOGGER.debug("Service not found", ex);
        }

        return service;
    }

    private Object tryLoadingBean(String beanName) {
        Object bean = null;
        try {
            bean = ServiceContext.getInstance().getApplicationContext().getBean(beanName);
        } catch (BeansException | APIException ex) {
            LOGGER.debug("Bean not found", ex);
        }
        return bean;
    }

    private void loadServices() {
        beansToLoad.clear();
        Map<String, String> services = util.parseStringToMap(configService.getServices());

        for (Map.Entry<String, String> entry : services.entrySet()) {
            beansToLoad.put(entry.getKey(), entry.getValue());
        }
    }

    private VelocityContext buildContext(List<Map<String, Object>> rows, Map<String, Object> params,
                                         List<Map<String, Object>> outputRows) {
        VelocityContext context = new VelocityContext();
        context.put(Constants.CONTEXT_PARAM_ROWS, rows);
        context.put(Constants.CONTEXT_PARAM_PARAMS, params);
        context.put(Constants.CONTEXT_PARAM_OUTS, outputRows);
        loadUtilsInContext(context);

        // Register the event handler class
        EventCartridge ec = new EventCartridge();
        ec.addEventHandler(this);
        ec.attachToContext(context);

        return context;
    }

    private String evalTemplate(String template, VelocityContext context, String key) throws IOException {
        StringWriter writer = new StringWriter();
        Velocity.evaluate(context, writer, key, template);
        if (ETL_LOAD.equals(key)) {
            LOGGER.debug(String.format("[L] Log : %s", writer.toString()));
        }
        return writer.toString();
    }

    private Mapping findMapping(String mappingName, Map<String, Object> params) throws MappingNotFoundException {
        String source = null;
        if (params.get(Constants.PARAM_SOURCE) != null) {
            source = params.get(Constants.PARAM_SOURCE).toString();
        }
        if (null == mappingName || null == source) {
            throw new IllegalArgumentException(MAPPING_ERROR);
        }

        Mapping mapping = mappingDao.findByNameAndSource(mappingName, source);

        if (null == mapping) {
            throw new MappingNotFoundException(MAPPING_NOT_FOUND + mappingName);
        }
        return mapping;
    }

    private void loadUtilsInContext(VelocityContext context) {
        context.put(Constants.CONTEXT_PARAM_UTIL, etlUtil);
        context.put("String", String.class);
        context.put("Integer", Integer.class);
        context.put("Long", Long.class);
        context.put("Float", Float.class);
        context.put("Double", Double.class);
        context.put("Date", Date.class);
        context.put("DateUtil", DateUtil.class);
        context.put("SimpleDateFormat", SimpleDateFormat.class);
        context.put("Calendar", Calendar.class);
        context.put("Math", Math.class);
    }

    /**
     * Get the last successful job run date or previous day date if no job data available for the specified database and mapping
     */
    private Date getLastRunDate(String database, String mapping) {
        Date lastRunDate = etlLogDao.executeQuery(database, mapping);
        return lastRunDate != null ? lastRunDate : DateUtil.plusDays(DateUtil.getDateWithLocalTimeZone(new Date()), -1);
    }

}

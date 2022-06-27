/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.handler.metadatasharing;

import org.apache.commons.lang.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.etllite.api.contract.ConfigRequestWrapper;
import org.openmrs.module.etllite.api.service.ConfigService;
import org.openmrs.module.metadatasharing.handler.MetadataPropertiesHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSaveHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSearchHandler;
import org.openmrs.module.metadatasharing.handler.MetadataTypesHandler;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The ETLServicesConfigHandler Class is a Metadata Sharing Handler which exposes ETL Services map to be exported and
 * imported by metadata Sharing module.
 * <p>
 * Bean configured in moduleApplicationContext.xml
 * </p>
 */
@OpenmrsProfile(modules = {"metadatasharing:1.*"})
public class ETLServicesConfigHandler
        implements MetadataTypesHandler<ETLServicesConfigWrapper>, MetadataSearchHandler<ETLServicesConfigWrapper>,
        MetadataSaveHandler<ETLServicesConfigWrapper>, MetadataPropertiesHandler<ETLServicesConfigWrapper> {

    private static final String NAME = "ETL Services Map";
    private static final int UUID_LENGTH = 34;

    private final Map<Class<? extends ETLServicesConfigWrapper>, String> types;

    public ETLServicesConfigHandler() {
        this.types = Collections.singletonMap(ETLServicesConfigWrapper.class, NAME);
    }

    @Override
    public ETLServicesConfigWrapper saveItem(ETLServicesConfigWrapper item) throws DAOException {
        final ConfigRequestWrapper configRequest = new ConfigRequestWrapper();
        configRequest.setServices(item.getValue());
        configRequest.setDatabases(getConfigService().allConfigs());
        getConfigService().createOrUpdateConfigs(configRequest);
        return item;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public Map<Class<? extends ETLServicesConfigWrapper>, String> getTypes() {
        return types;
    }

    @Override
    public int getItemsCount(Class<? extends ETLServicesConfigWrapper> type, boolean includeRetired, String phrase)
            throws DAOException {
        return getAllWrappers().size();
    }

    @Override
    public List<ETLServicesConfigWrapper> getItems(Class<? extends ETLServicesConfigWrapper> type, boolean includeRetired,
                                                   String phrase, Integer firstResult, Integer maxResults)
            throws DAOException {
        return getAllWrappers();
    }

    @Override
    public ETLServicesConfigWrapper getItemByUuid(Class<? extends ETLServicesConfigWrapper> type, String uuid)
            throws DAOException {
        return getAllWrappers().stream().findFirst().orElse(null);
    }

    @Override
    public ETLServicesConfigWrapper getItemById(Class<? extends ETLServicesConfigWrapper> type, Integer id)
            throws DAOException {
        return getAllWrappers().stream().findFirst().orElse(null);
    }

    @Override
    public Integer getId(ETLServicesConfigWrapper wrapper) {
        return wrapper.getValue().hashCode();
    }

    @Override
    public void setId(ETLServicesConfigWrapper wrapper, Integer id) {
        // nothing to do
    }

    @Override
    public String getUuid(ETLServicesConfigWrapper wrapper) {
        return StringUtils.abbreviate(wrapper.getValue(), UUID_LENGTH);
    }

    @Override
    public void setUuid(ETLServicesConfigWrapper wrapper, String uuid) {
        // nothing to do
    }

    @Override
    public Boolean getRetired(ETLServicesConfigWrapper wrapper) {
        return Boolean.FALSE;
    }

    @Override
    public void setRetired(ETLServicesConfigWrapper wrapper, Boolean retired) {
        // nothing to do
    }

    @Override
    public String getName(ETLServicesConfigWrapper wrapper) {
        return NAME;
    }

    @Override
    public String getDescription(ETLServicesConfigWrapper wrapper) {
        return wrapper.getValue();
    }

    @Override
    public Date getDateChanged(ETLServicesConfigWrapper wrapper) {
        return null;
    }

    @Override
    public Map<String, Object> getProperties(ETLServicesConfigWrapper wrapper) {
        return Collections.emptyMap();
    }

    private List<ETLServicesConfigWrapper> getAllWrappers() {
        final String servicesValue = getConfigService().getServices();

        if (StringUtils.isBlank(servicesValue)) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(new ETLServicesConfigWrapper(servicesValue));
        }
    }

    private ConfigService getConfigService() {
        return Context.getService(ConfigService.class);
    }

}

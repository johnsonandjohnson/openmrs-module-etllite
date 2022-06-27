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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.etllite.api.domain.Mapping;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class MappingDaoTest extends BaseModuleContextSensitiveTest {

    private static final String SOURCE = "source";
    private static final String NAME_1 = "name1";
    private static final String NAME_2 = "name2";

    @Autowired
    private MappingDao mappingDao;

    @Before
    public void setUp() {
        createMapping(NAME_1);
        createMapping(NAME_2);
    }

    @Test
    public void shouldFindById() {
        Mapping mapping = createMapping("name3");

        assertNotNull(mappingDao.findById(mapping.getId()));
    }

    @Test
    public void shouldCreateMapping() {
        Mapping mapping = createMapping("name4");

        assertNotNull(mapping);
    }

    @Test
    public void shouldUpdateMapping() {
        Mapping mapping = createMapping("name5");
        mapping.setTestResultsSize(2);

        mapping = mappingDao.update(mapping);

        assertThat(mapping.getTestResultsSize(), equalTo(2));
    }

    @Test
    public void shouldRetrieveAll() {
        List<Mapping> mappings = mappingDao.retrieveAll();

        assertThat(mappings.size(), equalTo(2));
    }

    @Test
    public void shouldDeleteAll() {
        mappingDao.deleteAll();

        assertThat(mappingDao.retrieveAll().size(), equalTo(0));
    }

    @Test
    public void shouldFindByNameAndSource() {
        Mapping mapping = mappingDao.findByNameAndSource(NAME_1, SOURCE);

        assertNotNull(mapping);
        assertThat(mapping.getName(), equalTo(NAME_1));
    }
    @Test
    public void shouldFindBySource() {
        List<Mapping> mappings = mappingDao.findBySource(SOURCE);

        assertThat(mappings.size(), equalTo(2));
    }

    private Mapping createMapping(String name) {
        Mapping mapping = new Mapping();
        mapping.setName(name);
        mapping.setSource(SOURCE);
        mapping.setQuery("query");
        mapping.setFetchSize(1);
        mapping.setTestResultsSize(1);

        return mappingDao.create(mapping);
    }
}

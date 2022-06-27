/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsMetadataDAO;
import org.openmrs.module.etllite.api.dao.MappingDao;
import org.openmrs.module.etllite.api.domain.Mapping;

import java.util.List;

public class MappingDaoImpl extends HibernateOpenmrsMetadataDAO<Mapping> implements MappingDao {

    private DbSessionFactory dbSessionFactory;

    public MappingDaoImpl() {
        super(Mapping.class);
    }

    @Override
    public Mapping findByNameAndSource(String name, String source) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("name", name));
        crit.add(Restrictions.eq("source", source));
        return (Mapping) crit.uniqueResult();
    }

    @Override
    public List<Mapping> findBySource(String source) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("source", source));
        return crit.list();
    }

    @Override
    public Mapping create(Mapping mapping) {
        return saveOrUpdate(mapping);
    }

    @Override
    public Mapping findById(Integer id) {
        return getById(id);
    }

    @Override
    public Mapping update(Mapping mapping) {
        return saveOrUpdate(mapping);
    }

    @Override
    public List<Mapping> retrieveAll() {
        return getAll(false);
    }

    @Override
    public void deleteAll() {
        getSession().createQuery("delete from etl.Mapping").executeUpdate();
    }

    public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
        this.dbSessionFactory = dbSessionFactory;
    }

    private DbSession getSession() {
        return dbSessionFactory.getCurrentSession();
    }
}

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

import org.openmrs.api.db.OpenmrsDataDAO;
import org.openmrs.module.etllite.api.domain.ETLLog;

import java.util.Date;
import java.util.List;

public interface ETLLogDao extends OpenmrsDataDAO<ETLLog> {
    ETLLog findById(Integer jobId);

    ETLLog update(ETLLog etlLog);

    ETLLog create(ETLLog etlLog);

    List<ETLLog> retrieveAll();

    void deleteAll();

    Date executeQuery(String database, String mapping);
}

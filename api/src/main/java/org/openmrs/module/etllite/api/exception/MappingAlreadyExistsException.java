/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.exception;

/**
 * Exception to indicate a mapping already exists if added as a duplicate
 *
 * @author nanakapa
 */
public class MappingAlreadyExistsException extends Exception {

    private static final long serialVersionUID = -7538733831136350170L;

    public MappingAlreadyExistsException(String message) {
        super(message);
    }

}

/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.util;

import org.apache.commons.io.IOUtils;
import org.openmrs.module.etllite.api.exception.ETLRuntimeException;

import java.io.IOException;
import java.io.InputStream;

public final class ResourceUtil {

    public static String readResourceFile(String fileName) throws ETLRuntimeException {
        try (InputStream in = ResourceUtil.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                throw new ETLRuntimeException("Resource '" + fileName + "' doesn't exist");
            }
            return IOUtils.toString(in);
        } catch (IOException e) {
            throw new ETLRuntimeException(e);
        }
    }

    private ResourceUtil() {
    }

}

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

import org.openmrs.util.Security;

public class EncryptionUtil {

    /**
     * This method takes plain text as input and return the base64 encoded string
     *
     * @param text plain text to be encrypted, if text is null or empty then encrypt method will
     *             return null
     * @return base 64 encoded string
     */
    public String encryptAsString(String text) {
        return Security.encrypt(text);
    }

    /**
     * This method takes base 64 encoded string as input and return the decrypted text
     *
     * @param encryptedText base 64 encoded text
     * @return decrypted plain text, null if the text to be decrypted is null.
     */
    public String decryptAsString(String encryptedText) {
        return Security.decrypt(encryptedText);
    }

}

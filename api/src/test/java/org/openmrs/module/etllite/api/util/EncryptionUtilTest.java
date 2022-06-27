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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.etllite.api.BaseTest;

import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit testcase for EncryptionUtil
 *
 * @author nanakapa
 */

@RunWith(MockitoJUnitRunner.class)
public class EncryptionUtilTest extends BaseTest {

    private static final String VECTOR_PROPERTY_KEY = "encryption.vector";

    private static final String KEY_PROPERTY_KEY = "encryption.key";

    private static final String SECRET_TEXT = "Secret text";

    private static final String DECRYPTED_SECRET_TEXT = "7z07+ocoVJBZ8zKqWD3URA==";

    private static final String EMPTY_STRING = "";

    @InjectMocks
    private EncryptionUtil encryptionUtil = new EncryptionUtil();

    @Before
    public void setUp() {
        Properties prop = new Properties();
        Context.setRuntimeProperties(prop);
    }

    @Test
    public void encryptAsStringShouldEncryptSuccessfullyDefaultProperties() {
        String actual = encryptionUtil.encryptAsString(SECRET_TEXT);
        assertThat(actual, is(DECRYPTED_SECRET_TEXT));
    }

    @Test
    public void decryptAsStringShouldDecryptSuccessfullyDefaultProperties() {
        String actual = encryptionUtil.decryptAsString(DECRYPTED_SECRET_TEXT);
        assertThat(actual, is(SECRET_TEXT));
    }

    @Test
    public void encryptAsStringShouldThrowApiExceptionWhenVectorPropertyIsMissing() {
        expectedException.expect(APIException.class);
        Properties prop = new Properties();
        prop.put(VECTOR_PROPERTY_KEY, EMPTY_STRING);
        Context.setRuntimeProperties(prop);
        encryptionUtil.encryptAsString(SECRET_TEXT);
    }

    @Test
    public void encryptAsStringShouldThrowApiExceptionWhenKeyPropertyIsMissing() {
        expectedException.expect(APIException.class);
        Properties prop = new Properties();
        prop.put(KEY_PROPERTY_KEY, EMPTY_STRING);
        Context.setRuntimeProperties(prop);
        encryptionUtil.encryptAsString(SECRET_TEXT);
    }

}

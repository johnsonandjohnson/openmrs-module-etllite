/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.validate;

import org.apache.commons.collections.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ObjectValidator<T> {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public void validate(T objectToValidate, Class<?> validationStep) {
        checkConfigViolations(validator.validate(objectToValidate, validationStep));
    }

    private void checkConfigViolations(Set<ConstraintViolation<T>> violations) {
        List<String> violationMessages = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(violations)) {
            for (ConstraintViolation<T> violation : violations) {
                violationMessages.add(violation.getMessage());
            }
            throw new IllegalArgumentException(violationMessages.toString());
        }
    }
}

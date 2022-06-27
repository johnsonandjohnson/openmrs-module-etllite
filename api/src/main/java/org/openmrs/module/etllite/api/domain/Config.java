/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.openmrs.module.etllite.api.domain.types.DatabaseTypes;
import org.openmrs.module.etllite.api.validate.ObjectValidator;
import org.openmrs.module.etllite.api.validate.ValueOfEnum;

import static org.openmrs.module.etllite.api.constants.Constants.INVALID_CONFIG_DB_PASSWORD_MESSAGE;
import static org.openmrs.module.etllite.api.constants.Constants.INVALID_CONFIG_NAME_MESSAGE;
import static org.openmrs.module.etllite.api.constants.Constants.INVALID_CONFIG_TYPE_MESSAGE;
import static org.openmrs.module.etllite.api.constants.Constants.INVALID_CONFIG_URL_MESSAGE;
import static org.openmrs.module.etllite.api.constants.Constants.INVALID_CONFIG_USER_MESSAGE;
import static org.openmrs.module.etllite.api.constants.Constants.INVALID_ENUM_TYPE_MESSAGE;

/**
 * Configuration entity, adapted from IVR Module This class encapsulates the ETL database
 * configuration, composed of as database url, username, database type, dbPassword and test query
 *
 * @author nanakapa
 */
public class Config {

  @NotBlank(groups = {ValidationStepOne.class, ValidationStepTwo.class}, message = INVALID_CONFIG_NAME_MESSAGE)
  private String name;

  @NotBlank(groups = {ValidationStepOne.class, ValidationStepTwo.class}, message = INVALID_CONFIG_TYPE_MESSAGE)
  @ValueOfEnum(enumClass = DatabaseTypes.class, groups = {ValidationStepOne.class, ValidationStepTwo.class},
      message = INVALID_ENUM_TYPE_MESSAGE)
  private String type;

  @NotBlank(groups = {ValidationStepOne.class, ValidationStepTwo.class}, message = INVALID_CONFIG_URL_MESSAGE)
  private String url;

  @NotBlank(groups = {ValidationStepOne.class, ValidationStepTwo.class}, message = INVALID_CONFIG_USER_MESSAGE)
  private String user;

  @NotBlank(groups = {ValidationStepTwo.class}, message = INVALID_CONFIG_DB_PASSWORD_MESSAGE)
  private String dbPassword;

  private String query;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getDbPassword() {
    return dbPassword;
  }

  public void setDbPassword(String dbPassword) {
    this.dbPassword = dbPassword;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public void validateUpdate() {
    new ObjectValidator<Config>().validate(this, ValidationStepOne.class);
  }

  public void validateSave() {
    new ObjectValidator<Config>().validate(this, ValidationStepTwo.class);
  }

  public interface ValidationStepOne {
  }

  public interface ValidationStepTwo {
  }
}

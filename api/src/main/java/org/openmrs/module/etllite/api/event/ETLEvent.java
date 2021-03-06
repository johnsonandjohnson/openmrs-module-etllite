/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.event;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openmrs.module.etllite.api.constants.Constants;
import org.openmrs.module.etllite.api.task.ETLTaskUtil;

import java.util.HashMap;
import java.util.Map;

public class ETLEvent {

  private String subject;
  private Map<String, Object> parameters;

  public ETLEvent(String subject, Map<String, Object> parameters) {
    this.subject = subject;
    this.parameters = parameters;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public Map<String, Object> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }

  public String getJobId() {
    return (String) getParameters().get(Constants.PARAM_JOB_ID);
  }

  public String generateTaskName() {
    return ETLTaskUtil.generateTaskName(getSubject(), getJobId());
  }

  public Map<String, String> convertProperties() {
    Map<String, String> result = new HashMap<>(getParameters().size());

    for (String key : getParameters().keySet()) {
      result.put(key, (String) getParameters().get(key));
    }

    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
}

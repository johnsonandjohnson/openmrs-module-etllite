/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.event.Event;
import org.openmrs.event.EventMessage;
import org.openmrs.module.etllite.api.event.ETLEvent;
import org.openmrs.module.etllite.api.service.ETLEventService;

import java.io.Serializable;
import java.util.Map;

public class ETLEventServiceImpl extends BaseOpenmrsService implements ETLEventService {

  @Override
  public void sendEventMessage(ETLEvent event) {
    Event.fireEvent(event.getSubject(), convertParamsToEventMessage(event.getParameters()));
  }

  private EventMessage convertParamsToEventMessage(Map<String, Object> params) {
    final EventMessage eventMessage = new EventMessage();

    for (Map.Entry<String, Object> param : params.entrySet()) {
      eventMessage.put(param.getKey(), (Serializable) param.getValue());
    }

    return eventMessage;
  }
}

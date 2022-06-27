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

import org.openmrs.api.context.Daemon;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.etllite.api.exception.ETLRuntimeException;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractETLEventListener implements ETLEventListener {

  private DaemonToken daemonToken;

  @Override
  public void onMessage(Message message) {
    try {
      // OpenMRS event module uses underneath MapMessage to construct Message. For some reason retrieving properties
      // from Message interface doesn't work and we have to map object to MapMessage.
      final Map<String, Object> properties = getProperties((MapMessage) message);
      Daemon.runInDaemonThread(() -> handleEvent(properties), daemonToken);
    } catch (JMSException ex) {
      throw new ETLRuntimeException("Error during handling ETL event", ex);
    }
  }

  @Override
  public void setDaemonToken(DaemonToken daemonToken) {
    this.daemonToken = daemonToken;
  }

  protected abstract void handleEvent(Map<String, Object> properties);

  private Map<String, Object> getProperties(MapMessage mapMessage) throws JMSException {
    final Enumeration<?> propertiesKey = mapMessage.getMapNames();

    Map<String, Object> properties = new HashMap<>();

    while (propertiesKey.hasMoreElements()) {
      String key = (String) propertiesKey.nextElement();
      properties.put(key, mapMessage.getObject(key));
    }

    return properties;
  }
}

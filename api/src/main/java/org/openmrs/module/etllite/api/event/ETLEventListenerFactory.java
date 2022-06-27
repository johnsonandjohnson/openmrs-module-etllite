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

import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.DaemonToken;

public final class ETLEventListenerFactory {

    private static ETLEventListener etlFailureEventListener;
    private static ETLEventListener etlImportDataEventListener;
    private static ETLEventListener etlJobEventListener;
    private static DaemonToken daemonToken;
    private static final Object LISTENER_LOCK = new Object();

    public static void registerEventListeners() {
        synchronized (LISTENER_LOCK) {
            etlFailureEventListener = Context.getRegisteredComponent(
                    "etllite.ETLFailureEventListener", ETLEventListener.class);
            etlFailureEventListener.setDaemonToken(daemonToken);
            subscribeListener(etlFailureEventListener);

            etlImportDataEventListener = Context.getRegisteredComponent(
                    "etllite.ETLImportDataEventListener", ETLEventListener.class);
            etlImportDataEventListener.setDaemonToken(daemonToken);
            subscribeListener(etlImportDataEventListener);

            etlJobEventListener = Context.getRegisteredComponent(
                    "etllite.ETLJobEventListener", ETLEventListener.class);
            etlJobEventListener.setDaemonToken(daemonToken);
            subscribeListener(etlJobEventListener);
        }
    }

    public static void unRegisterEventListeners() {
        if (etlFailureEventListener != null) {
            unSubscribeListener(etlFailureEventListener);
        }
        if (etlImportDataEventListener != null) {
            unSubscribeListener(etlImportDataEventListener);
        }
        if (etlJobEventListener != null) {
            unSubscribeListener(etlJobEventListener);
        }
    }

    public static void setDaemonToken(DaemonToken daemonToken) {
        ETLEventListenerFactory.daemonToken = daemonToken;
        synchronized (LISTENER_LOCK) {
            if (etlFailureEventListener != null) {
                etlFailureEventListener.setDaemonToken(daemonToken);
            }
            if (etlImportDataEventListener != null) {
                etlImportDataEventListener.setDaemonToken(daemonToken);
            }
            if (etlJobEventListener != null) {
                etlJobEventListener.setDaemonToken(daemonToken);
            }
        }
    }

    private static void subscribeListener(ETLEventListener etlEventListener) {
        Event.subscribe(etlEventListener.getSubject(), etlEventListener);
    }

    private static void unSubscribeListener(ETLEventListener etlEventListener) {
        Event.unsubscribe(etlEventListener.getSubject(), etlEventListener);
    }

    private ETLEventListenerFactory() { }
}

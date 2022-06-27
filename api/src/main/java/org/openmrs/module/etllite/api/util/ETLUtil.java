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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Defines utilities methods that will be injected in transform and load templates by default under
 * the key $util
 *
 * @author nanakapa
 */
public class ETLUtil {

  private static final Log LOGGER = LogFactory.getLog(ETLUtil.class);

  /**
   * Creates and returns a new object instance using Context from OpenMRS
   *
   * @param className Class name to create a new instance
   * @return new instance of the specified class, null if there is any exception while creating a
   * new instance of the specified class
   */
  public Object newObject(String className) {
    try {
      Class<?> clazz = loadClass(className);

      if (null != clazz) {
        return clazz.newInstance();
      }
    } catch (InstantiationException | IllegalAccessException e) {
      LOGGER.error(String.format("Error in creating a new object for the class %s and exception is :", className), e);
    }
    return null;
  }

  /**
   * Loads class from OpenMRS Context
   *
   * @param className Class name to be loaded
   * @return the specified class, null if there is any exception while loading the specified class
   */
  public Class<?> loadClass(String className) {
    try {
      return Context.loadClass(className);
    } catch (ClassNotFoundException e) {
      LOGGER.error(String.format("The class %s not found and exception is : ", className), e);
    }
    return null;
  }

  /**
   * This method converts the date object to the required string format
   *
   * @param date   <code>Date</code>
   * @param format pattern to be converted
   * @return formatted date
   */

  public String formatDate(Date date, String format) {
    return DateUtil.dateToString(date, format, DateUtil.getLocalTimeZone());
  }

  /**
   * Creates new HashMap
   *
   * @return new HashMap
   */
  public Map<String, Object> newMap() {
    return new HashMap<String, Object>();
  }

  /**
   * Converts String to Long
   *
   * @param value string value to be converted to Long
   * @return Long value
   */
  public Long toLong(String value) {
    return Long.valueOf(value);
  }

  /**
   * Converts date in string format to <code>Date</code>
   *
   * @param date   in string format
   * @param format pattern of the input date format
   * @return <code>Date</code>
   */
  public Date stringToDate(String date, String format) {
    return DateUtil.parse(date, format);
  }

  /**
   * Creates new HashSet
   *
   * @return new HashSet
   */
  public Set<Object> newSet() {
    return new HashSet<Object>();
  }

  /**
   * Create a Locale object from language and country
   *
   * @param language An ISO 639 alpha-2 or alpha-3 language code, or a language subtag
   * @return <code>Locale</code>
   */
  public Locale getLocale(String language) {
    return new Locale(language);
  }

  /**
   * Returns the current date
   *
   * @return <code>Date</code>
   */
  public Date today() {
    return new Date();
  }
}

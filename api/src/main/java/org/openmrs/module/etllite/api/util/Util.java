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

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Util Class
 */
public class Util {

  private static final int DEFAULT_FETCH_SIZE = 1000;

  /**
   * This method creates <code>NamedParameterJdbcTemplate</code> using the datasource
   *
   * @param dataSource <code>DataSource</code>
   * @param fetchSize  parameter to retrieve the data in chunks
   * @return <code>NamedParameterJdbcTemplate</code>
   */
  public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(DataSource dataSource, int fetchSize) {
    if (null == dataSource) {
      throw new IllegalArgumentException("datasource can not be null");
    }
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    jdbcTemplate.setFetchSize(fetchSize != 0 ? fetchSize : DEFAULT_FETCH_SIZE);
    return new NamedParameterJdbcTemplate(jdbcTemplate);
  }

  /**
   * This method converts comma separated string to Map
   *
   * @param string comma separated string
   * @return <code>Map</code>
   */
  public Map<String, String> parseStringToMap(String string) {
    Map<String, String> map = new HashMap<>();
    if (StringUtils.isBlank(string)) {
      return map;
    }
    String[] strings = string.split("\\s*,\\s*");
    for (String s : strings) {
      String[] kv = s.split("\\s*:\\s*");
      if (kv.length == 2) {
        map.put(kv[0], kv[1]);
      } else {
        throw new IllegalArgumentException(String.format("%s is an invalid map", string));
      }
    }
    return map;
  }
}

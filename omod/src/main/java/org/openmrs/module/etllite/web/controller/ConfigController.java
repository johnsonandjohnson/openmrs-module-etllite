/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.openmrs.module.etllite.api.builder.ConfigResponseBuilder;
import org.openmrs.module.etllite.api.contract.ConfigRequestWrapper;
import org.openmrs.module.etllite.api.contract.ConfigResponse;
import org.openmrs.module.etllite.api.contract.ConfigResponseWrapper;
import org.openmrs.module.etllite.api.domain.Config;
import org.openmrs.module.etllite.api.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller to manage ETL database settings, adapted from IVR module
 *
 * @author nanakapa
 */
@Api(value = "Settings")
@RequestMapping(value = "/etllite/configs")
@Controller
public class ConfigController extends RestController {

  @Autowired
  @Qualifier("etllite.configService")
  private ConfigService configService;

  @Autowired
  @Qualifier("etllite.ConfigResponseBuilder")
  private ConfigResponseBuilder configResponseBuilder;

  /**
   * This method tests the specified ETL database connectivity by using the parameters defined while creating the ETL
   * database settings.
   *
   * @param name ETL database name to test the connectivity
   * @return true, if the connection to the database is successful, false if not
   * @throws SQLException
   */
  @RequestMapping(value = "/{name}/test", method = RequestMethod.GET)
  @ApiOperation(value = "Test a ETL database connectivity",
      notes = "Return true if the specified ETL database connection is successful, false if not", response = Boolean.class)
  @ApiResponses(
      value = {@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Connectivity to the source database is successful"),
          @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR,
              message = "Connectivity to the source database is not successful")})
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public boolean testDatabase(
      @ApiParam(name = "name", value = "Name of the ETL database settings to be tested", required = true)
      @PathVariable(value = "name") String name) throws SQLException {
    return configService.testDatabase(name);
  }

  /**
   * This method fetches all the ETL database settings
   *
   * @return <code>ConfigResponseWrapper</code> list of current ETL database settings and spring services
   */
  @RequestMapping(method = RequestMethod.GET)
  @ApiOperation(value = "Retrieves list of source database settings and Spring services defined in the system",
      notes = "Retrieves list of source database settings and Spring services defined in the system",
      response = ConfigResponseWrapper.class)
  @ApiResponses(
      value = {@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Successful retrieval of source database settings")})
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ConfigResponseWrapper getConfigs() {
    return createResponseForAllConfigs();
  }

  /**
   * This method creates or updates the ETL database settings and spring services
   *
   * @param configRequestWrapper list of database settings and services to be created or updated
   * @return list of database configurations and spring services
   */
  @RequestMapping(method = RequestMethod.POST)
  @ApiOperation(value = "Create or Update a source database settings and spring services",
      notes = "Create or Update a source database settings and spring services", response = ConfigResponseWrapper.class)
  @ApiResponses(value = {
      @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful update of the source database settings")})
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ConfigResponseWrapper updateConfigs(@ApiParam(name = "configRequestWrapper",
      value = "Details of source database configurations and spring services to be created or updated", required = true)
                                             @RequestBody ConfigRequestWrapper configRequestWrapper) {
    configService.createOrUpdateConfigs(configRequestWrapper);
    return createResponseForAllConfigs();
  }

  /**
   * Deletes a specific ETL database setting
   *
   * @param name ETL database name
   */
  @RequestMapping(value = "/{name}", method = RequestMethod.DELETE)
  @ApiOperation(value = "Deletes a specific source database settings", notes = "Deletes a specific source database settings")
  @ApiResponses(value = {
      @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Successful deletion of the source database settings")})
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void delete(@ApiParam(name = "name", value = "Name of the source database settings to be deleted", required = true)
                     @PathVariable(value = "name") String name) {
    configService.deleteConfig(name);
  }

  /**
   * Delete all ETL database settings
   */
  @RequestMapping(method = RequestMethod.DELETE)
  @ApiOperation(value = "Delete all source database settings", notes = "Delete all source database settings")
  @ApiResponses(value = {
      @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Successful deletion of all source database settings")})
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void deleteAll() {
    configService.deleteAll();
  }

  private ConfigResponseWrapper createResponseForAllConfigs() {
    List<Config> configs = configService.allConfigs();

    List<ConfigResponse> configResponses = new ArrayList<>(configs.size());
    for (Config config : configs) {
      configResponses.add(configResponseBuilder.createFrom(config));
    }

    final ConfigResponseWrapper configResponseWrapper = new ConfigResponseWrapper();
    configResponseWrapper.setDatabases(configResponses);
    configResponseWrapper.setServices(configService.getServices());
    return configResponseWrapper;
  }
}

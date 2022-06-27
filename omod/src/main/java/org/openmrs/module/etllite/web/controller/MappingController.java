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
import org.openmrs.module.etllite.api.builder.MappingRequestBuilder;
import org.openmrs.module.etllite.api.builder.MappingResponseBuilder;
import org.openmrs.module.etllite.api.contract.MappingRequest;
import org.openmrs.module.etllite.api.contract.MappingResponse;
import org.openmrs.module.etllite.api.domain.Mapping;
import org.openmrs.module.etllite.api.exception.ETLException;
import org.openmrs.module.etllite.api.exception.MappingAlreadyExistsException;
import org.openmrs.module.etllite.api.exception.MappingNotFoundException;
import org.openmrs.module.etllite.api.service.MappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Mapping Controller to manage the ETL mappings
 *
 * @author nanakapa
 */
@Api(value = "Mappings")
@RequestMapping(value = "/etllite/mappings")
@Controller
public class MappingController extends RestController {

    private static final String LOOKUP_BY_SOURCE_PREFIX = "By Source";
    private static final String LOOKUP_BY_ID_PREFIX = "By Id";

    @Autowired
    @Qualifier("etllite.MappingResponseBuilder")
    private MappingResponseBuilder mappingResponseBuilder;

    @Autowired
    @Qualifier("etllite.MappingRequestBuilder")
    private MappingRequestBuilder mappingRequestBuilder;

    @Autowired
    @Qualifier("etllite.mappingService")
    private MappingService mappingService;

    /**
     * REST API to create a new ETL mapping.
     *
     * @param mappingRequest contains the details to create a mapping
     * @return a response contract of the created mapping
     * @throws MappingAlreadyExistsException
     */

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Add a new ETL mapping",
            notes = "Add a new ETL mapping",
            response = MappingResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful creation of the ETL Lite mapping"),
            @ApiResponse(code = HttpURLConnection.HTTP_CONFLICT, message = "Mapping already exists in the system")})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MappingResponse create(
            @ApiParam(name = "mappingRequest", value = "MappingRequest", required = true) @RequestBody MappingRequest mappingRequest)
            throws MappingAlreadyExistsException {
        Mapping mapping = mappingRequestBuilder.createFrom(mappingRequest);
        return mappingResponseBuilder.createFrom(mappingService.create(mapping));
    }

    /**
     * REST API to find the mappings for the specified ETL source or for the specified mapping
     *
     * @param lookup a lookup term - currently supports "By Source" and "By Id"
     * @param source ETL database
     * @param id     ETL mapping id
     * @return list of response contract objects
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Retrieves list of ETL Lite mappings defined in the system",
            notes = "Retrieves list of ETL Lite mappings defined in the system for a specific source database or specific mapping id" +
                    " if the lookup parameter is present in the request object. Otherwise retrieve all mappings",
            response = MappingResponse.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful retrieval of ETL Lite mappings")})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<MappingResponse> findMappings(
            @ApiParam(name = "lookup", value = "Returns list of mappings for a specified source if the lookup parameter" +
                    " is present with value \"By Source\" in the request", required = false)
            @RequestParam(value = "lookup", required = false) String lookup,
            @ApiParam(name = "source", value = "source system name defined in the ETL settings", required = false)
            @RequestParam(value = "source", required = false) String source,
            @ApiParam(name = "id", value = "Mapping id", required = false)
            @RequestParam(value = "id", required = false) Integer id) {

        List<Mapping> mappings = new ArrayList<>();

        if (LOOKUP_BY_SOURCE_PREFIX.equals(lookup)) {
            mappings = mappingService.findBySource(source);
            return buildMappingResponse(mappings);
        }

        if (LOOKUP_BY_ID_PREFIX.equals(lookup)) {
            Mapping mapping = mappingService.findById(id);
            if (null != mapping) {
                mappings.add(mapping);
            }
            return buildMappingResponse(mappings);
        }
        return buildMappingResponse(mappingService.findAll());
    }

    /**
     * REST API to update a ETL mapping
     *
     * @param id             mapping id
     * @param mappingRequest contains the details to update a mapping
     * @return a response contract of the updated mapping
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Updates a specific ETL mapping",
            notes = "Updates a specific ETL mapping",
            response = MappingResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Successful update of the ETL Lite mapping"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Mapping does not exists in the system")})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MappingResponse update(
            @ApiParam(name = "id", value = "Id of the mapping to be updated", required = true) @PathVariable(value = "id") Integer id,
            @ApiParam(name = "mappingRequest", value = "Details of the mapping to be updated", required = true) @RequestBody MappingRequest mappingRequest)
            throws MappingNotFoundException {
        Mapping mapping = mappingRequestBuilder.createFrom(mappingRequest);
        mapping.setId(id);
        return mappingResponseBuilder.createFrom(mappingService.update(mapping));
    }

    /**
     * REST API to delete a specific mapping
     *
     * @param id mapping id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Deletes a specific ETL mapping",
            notes = "Deletes a specific ETL mapping")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Successful deletion of the ETL Lite mapping"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Mapping does not exists in the system")})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void delete(
            @ApiParam(name = "id", value = "Id of the mapping to be deleted", required = true) @PathVariable(value = "id") Integer id)
            throws MappingNotFoundException {
        mappingService.delete(id);
    }

    /**
     * Test a specific mapping and returns extracted and mapping results
     *
     * @param id mapping id to be tested
     * @return test data map
     * @throws ETLException
     */
    @RequestMapping(value = "/{id}/test", method = RequestMethod.GET)
    @ApiOperation(value = "Test a specific ETL mapping",
            notes = "Test a specific mapping and returns extracted and transformed results",
            response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful testing of the mapping"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Mapping does not exists in the system"),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Internal Error")})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, List<Map<String, Object>>> testMapping(
            @ApiParam(name = "id", value = "Id of the mapping to be tested", required = true) @PathVariable(value = "id") Integer id)
            throws ETLException, MappingNotFoundException, IOException {
        return mappingService.testMapping(id);
    }

    private List<MappingResponse> buildMappingResponse(List<Mapping> mappings) {
        List<MappingResponse> mappingResponseList = new ArrayList<>(mappings.size());
        for (Mapping mapping : mappings) {
            mappingResponseList.add(mappingResponseBuilder.createFrom(mapping));
        }
        return mappingResponseList;
    }
}

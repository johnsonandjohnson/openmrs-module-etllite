/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import axiosInstance from '../components/shared/axios'
import { SUCCESS, REQUEST, FAILURE } from './action-type.util';
import { ConfigModel } from '../shared/model/config.model';
import { DatabaseModel } from '../shared/model/database.model';
import _ from 'lodash';
import { handleRequest } from '../utils/request-status-util';
import { DBConnectionType } from '../shared/model/DBConnectionType';
import { errorToast, successToast } from '../utils/toast-display-util';

export const ACTION_TYPES = {
  RESET: 'configReducer/RESET',
  FETCH_CONFIGS: 'configReducer/FETCH_CONFIGS',
  POST_CONFIGS: 'configReducer/POST_CONFIGS',
  UPDATE_SERVICES_AFTER_CHANGE: 'configReducer/UPDATE_SERVICES_AFTER_CHANGE',
  ADD_EMPTY_DATABASE: 'configReducer/ADD_EMPTY_DATABASE',
  REMOVE_DATABASE: 'configReducer/REMOVE_DATABASE',
  REMOVE_LOCAL_DATABASE: 'configReducer/REMOVE_LOCAL_DATABASE',
  UPDATE_DATABASE_AFTER_CHANGE: 'configReducer/UPDATE_DATABASE_AFTER_CHANGE',
  TEST_DATABASE: 'configReducer/TEST_DATABASE',
  OPEN_MODAL: 'configReducer/OPEN_MODAL',
  CLOSE_MODAL: 'configReducer/CLOSE_MODAL',
  UPDATE_CONFIG_LOCALLY: 'configReducer/UPDATE_CONFIG_LOCALLY'
};

const initialState = {
  config: new ConfigModel(),
  showModal: false,
  idToDelete: null,
  isLoading: false
};

export default (state = initialState, action) => {
  switch (action.type) {
    // isLoading flag is not set as the response might took long and the request is not blocking
    case REQUEST(ACTION_TYPES.TEST_DATABASE):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.TEST_DATABASE):
      errorToast(action.payload.response.data ? action.payload.response.data.message : "Database testing failure");
      return {
        ...state
      };
    case REQUEST(ACTION_TYPES.FETCH_CONFIGS):
    case REQUEST(ACTION_TYPES.POST_CONFIGS):
    case REQUEST(ACTION_TYPES.REMOVE_DATABASE):
      return {
        ...state,
        isLoading: true
      };
    case FAILURE(ACTION_TYPES.FETCH_CONFIGS):
    case FAILURE(ACTION_TYPES.POST_CONFIGS):
    case FAILURE(ACTION_TYPES.REMOVE_DATABASE):
      return {
        ...state,
        isLoading: false
      };
    case SUCCESS(ACTION_TYPES.FETCH_CONFIGS):
    case SUCCESS(ACTION_TYPES.POST_CONFIGS):
      return {
        ...state,
        config: new ConfigModel(action.payload.data),
        isLoading: false
      };
    case ACTION_TYPES.UPDATE_SERVICES_AFTER_CHANGE:
      return {
        ...state,
        config: updateServices(state.config, action.payload)
      }
    case ACTION_TYPES.UPDATE_DATABASE_AFTER_CHANGE:
      return {
        ...state,
        config: updateDatabaseAfterChange(state.config, action.payload, action.meta)
      }
    case ACTION_TYPES.ADD_EMPTY_DATABASE:
      return {
        ...state,
        config: addEmptyDatabase(state.config)
      }
    case SUCCESS(ACTION_TYPES.TEST_DATABASE):
      return {
        ...state,
        config: updateDatabaseTestsResult(state.config, action.payload, action.meta)
      }
    case SUCCESS(ACTION_TYPES.REMOVE_DATABASE):
      return {
        ...state,
        showModal: false,
        idToDelete: null,
        config: removeLocalDatabase(state.config, action.meta.localId),
        isLoading: false
      }
    case ACTION_TYPES.REMOVE_LOCAL_DATABASE:
      return {
        ...state,
        config: removeLocalDatabase(state.config, action.payload)
      }
    case ACTION_TYPES.OPEN_MODAL: {
      return {
        ...state,
        showModal: true,
        idToDelete: action.payload
      };
    }
    case ACTION_TYPES.UPDATE_CONFIG_LOCALLY: {
      return {
        ...state,
        config: action.payload
      };
    }
    case ACTION_TYPES.CLOSE_MODAL: {
      return {
        ...state,
        showModal: false,
        idToDelete: null
      };
    }
    case ACTION_TYPES.RESET: {
      return initialState;
    }
    default:
      return state;
  }
};

const configsUrl = 'ws/etllite/configs';

export const openModal = (idToDelete) => ({
  type: ACTION_TYPES.OPEN_MODAL,
  payload: idToDelete
});

export const closeModal = () => ({
  type: ACTION_TYPES.CLOSE_MODAL
});

export const reset = () => ({
  type: ACTION_TYPES.RESET
});

export const getConfig = () => ({
  type: ACTION_TYPES.FETCH_CONFIGS,
  payload: axiosInstance.get(configsUrl)
});

export const addDatabase = () => ({
  type: ACTION_TYPES.ADD_EMPTY_DATABASE
});

export const changeServices = event => ({
  type: ACTION_TYPES.UPDATE_SERVICES_AFTER_CHANGE,
  payload: event.target.value
});

export const changeDatabase = (event, localId) => ({
  type: ACTION_TYPES.UPDATE_DATABASE_AFTER_CHANGE,
  payload: event.target,
  meta: { localId }
});

export const updateConfigLocally = newConfig => ({
  type: ACTION_TYPES.UPDATE_CONFIG_LOCALLY,
  payload: newConfig
});

export const removeDatabase = (databases, localId, removeLocally) => async dispatch => {
  if (removeLocally) {
    dispatch({
      type: ACTION_TYPES.REMOVE_LOCAL_DATABASE,
      payload: localId
    });
  } else {
    const db = getDbByLocalId(databases, localId);
    const body = {
      type: ACTION_TYPES.REMOVE_DATABASE,
      payload: axiosInstance.delete(`${configsUrl}/${db.name}`),
      meta: { databases, localId }
    }
    handleRequest(dispatch, body, "Database deleted successfully!", "Database deletion failure!");
  }
};

export const updateConfig = config => async dispatch => {
  const body = {
    type: ACTION_TYPES.POST_CONFIGS,
    payload: axiosInstance.post(configsUrl, config.toRequest())
  }
  handleRequest(dispatch, body, "Configuration saved successfully!", "Configuration update failure!");
};

export const testDatabase = db => ({
  type: ACTION_TYPES.TEST_DATABASE,
  payload: axiosInstance.get(`${configsUrl}/${db.name}/test`),
  meta: { localId: db.localId }
});

const removeLocalDatabase = (currentConfig, localId) => {
  let config = _.cloneDeep(currentConfig);
  _.remove(config.databases, function (db) {
    return db.localId === localId;
  });
  return config;
};

const updateDatabaseAfterChange = (currentConfig, field, meta) => {
  let config = _.cloneDeep(currentConfig);
  let db = getDbByLocalId(config.databases, meta.localId);
  db[field.name] = field.value;
  changeUrlIfDbTypeWasUpdated(field, db);
  return config;
};

const changeUrlIfDbTypeWasUpdated = (field, db) => {
  if (field.name === "type") {
    db.url = DBConnectionType.find(function (option) {
      return option.value === field.value;
    }).defaultUrl;
    if (db.errors) {
      db.errors = _.omit(db.errors, 'url');
    }
  }
};

const addEmptyDatabase = currentConfig => {
  let config = _.cloneDeep(currentConfig);
  config.databases = config.databases.concat(new DatabaseModel());
  return config;
};

const updateDatabaseTestsResult = (currentConfig, payload, meta) => {
  let config = _.cloneDeep(currentConfig);
  let db = getDbByLocalId(config.databases, meta.localId);
  if (payload.data) {
    successToast("Database tested successfully");
    db.testResult = "SUCCESS";
  } else {
    errorToast("Database testing failure");
    db.testResult = "FAILURE";
  }
  return config;
}

const updateServices = (currentConfig, services) => {
    const config = _.cloneDeep(currentConfig);
    config.services = services;
    return config;
};

const getDbByLocalId = (databases, localId) => {
  return databases.find(function (db) {
    return db.localId === localId;
  });
};

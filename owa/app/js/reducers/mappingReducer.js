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
import { MappingModel } from '../shared/model/mapping.model';
import * as Default from '../utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import { handleRequest } from '../utils/request-status-util';

export const ACTION_TYPES = {
  RESET: 'mappingReducer/RESET',
  FETCH_MAPPINGS: 'mappingReducer/FETCH_MAPPINGS',
  CREATE_MAPPING: 'mappingReducer/CREATE_MAPPING',
  UPDATE_MAPPING: 'mappingReducer/UPDATE_MAPPING',
  FETCH_SOURCES: 'mappingReducer/FETCH_SOURCES',
  ADD_NEW_EMPTY: 'mappingReducer/ADD_NEW_EMPTY',
  REMOVE_MAPPING: 'mappingReducer/REMOVE_MAPPING',
  REMOVE_LOCAL_MAPPING: 'mappingReducer/REMOVE_LOCAL_MAPPING',
  UPDATE_MAPPING_AFTER_CHANGE: 'mappingReducer/UPDATE_MAPPING_AFTER_CHANGE',
  TEST_MAPPING: 'mappingReducer/TEST_MAPPING',
  OPEN_MODAL: 'mappingReducer/OPEN_MODAL',
  CLOSE_MODAL: 'mappingReducer/CLOSE_MODAL',
};

const initialState = {
  mappings: [],
  sources: [],
  showModal: false,
  mappingToDelete: null
};

export default (state = initialState, action) => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_MAPPINGS):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.FETCH_MAPPINGS):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.FETCH_MAPPINGS):
      return {
        ...state,
        mappings: action.payload.data.map((mappingResponse) => {return new MappingModel(mappingResponse, false)})
      };
    case REQUEST(ACTION_TYPES.CREATE_MAPPING):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.CREATE_MAPPING):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.CREATE_MAPPING):
      let newValue = new MappingModel(action.payload.data, action.isOpen);
      newValue.uiLocalUuid = action.meta.uiLocalUuid;
      return {
        ...state,
        mappings: replaceMapping(state.mappings, newValue)
      };
    case REQUEST(ACTION_TYPES.UPDATE_MAPPING):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.UPDATE_MAPPING):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.UPDATE_MAPPING):
      return {
        ...state,
        mappings: markMappingTestable(state.mappings, action.payload.data.id)
      };
    case REQUEST(ACTION_TYPES.FETCH_SOURCES):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.FETCH_SOURCES):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.FETCH_SOURCES):
      return {
        ...state,
        sources: action.payload.data['databases'].map((config) => {
          return config.name;
        })
      };
    case REQUEST(ACTION_TYPES.TEST_MAPPING):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.TEST_MAPPING):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.TEST_MAPPING):
      return {
        ...state,
        mappings: addTestResults(state.mappings, action)
      };
    case ACTION_TYPES.ADD_NEW_EMPTY:
      return {
        ...state,
        mappings: state.mappings.concat(new MappingModel(null, true))
      }
    case ACTION_TYPES.UPDATE_MAPPING_AFTER_CHANGE:
      return {
        ...state,
        mappings: replaceMapping(state.mappings, action.payload)
      }
    case REQUEST(ACTION_TYPES.REMOVE_MAPPING):
    case FAILURE(ACTION_TYPES.REMOVE_MAPPING):
      return {
        ...state
      }
    case SUCCESS(ACTION_TYPES.REMOVE_MAPPING):
      return {
        ...state,
        showModal: false,
        mappingToDelete: null,
        mappings: removeFromMappings(state.mappings, action.meta.uiLocalUuid)
      }
    case ACTION_TYPES.REMOVE_LOCAL_MAPPING:
        return {
          ...state,
          mappings: removeFromMappings(state.mappings, action.meta.uiLocalUuid)
        }
    case ACTION_TYPES.OPEN_MODAL: {
      return {
        ...state,
        showModal: true,
        mappingToDelete: action.payload
      };
    }
    case ACTION_TYPES.CLOSE_MODAL: {
      return {
        ...state,
        showModal: false,
        mappingToDelete: null
      };
    }
    case ACTION_TYPES.RESET: {
      return initialState;
    }
    default:
      return state;
  }
};

const replaceMapping = (mappings, changedMapping) => {
  return mappings.map((item) => {
    if (item.uiLocalUuid === changedMapping.uiLocalUuid) {
      item = changedMapping;
    }
    return item;
  });
}

const addTestResults = (mappings, action) => {
  let mappingId = action.meta;
  return mappings.map((m) => {
    if (m.id === mappingId) {
      m.testResults = action.payload.data;
    }
    return m;
  });
}

const removeFromMappings = (mappings, uiLocalUuid) => {
  return mappings.filter((item) => item.uiLocalUuid !== uiLocalUuid);
}

const markMappingTestable = (mappings, mappingId) => {
  return mappings.map((item) => {
    if (item.id === mappingId) {
      item.isTestable = true;
      item.testResults = null;
    }
    return item;
  });
}

export const reset = () => ({
  type: ACTION_TYPES.RESET
});

export const getMappings = () => async (dispatch) => {
  const requestUrl = 'ws/etllite/mappings';
  await dispatch({
    type: ACTION_TYPES.FETCH_MAPPINGS,
    payload: axiosInstance.get(requestUrl)
  });
};

export const addNew = () => ({
  type: ACTION_TYPES.ADD_NEW_EMPTY
});

export const changeMapping = (newMapping) => ({
  type: ACTION_TYPES.UPDATE_MAPPING_AFTER_CHANGE,
  payload: newMapping
});

export const createMapping = (mappingRequest) => async (dispatch) => {
  const requestUrl = 'ws/etllite/mappings';
  let body = {
      type: ACTION_TYPES.CREATE_MAPPING,
      meta: {
        isOpen: mappingRequest.isOpen,
        uiLocalUuid: mappingRequest.uiLocalUuid
      },
      payload: axiosInstance.post(requestUrl, mappingRequest.toRequest())
  };
  handleRequest(dispatch, body,
    getIntl().formatMessage({ id: 'ETL_MAPPING_CREATE_SUCCESS', defaultMessage: Default.MAPPING_CREATE_SUCCESS }),
    getIntl().formatMessage({ id: 'ETL_MAPPING_CREATE_FAILURE', defaultMessage: Default.MAPPING_CREATE_FAILURE }));
};

export const updateMapping = (mappingRequest) => async (dispatch) => {
  const requestUrl = 'ws/etllite/mappings';
  const body = {
    type: ACTION_TYPES.UPDATE_MAPPING,
    payload: axiosInstance.put(requestUrl + "/" + mappingRequest.id, mappingRequest.toRequest())
  };
  handleRequest(dispatch, body,
    getIntl().formatMessage({ id: 'ETL_MAPPING_UPDATE_SUCCESS', defaultMessage: Default.MAPPING_UPDATE_SUCCESS }),
    getIntl().formatMessage({ id: 'ETL_MAPPING_UPDATE_FAILURE', defaultMessage: Default.MAPPING_UPDATE_FAILURE }));
};

export const openModal = (mappingToDelete) => ({
  type: ACTION_TYPES.OPEN_MODAL,
  payload: mappingToDelete
});

export const closeModal = () => ({
  type: ACTION_TYPES.CLOSE_MODAL
});

export const removeMapping = (mappingRequest) => async (dispatch) => {
  const requestUrl = 'ws/etllite/mappings';
  const { id, uiLocalUuid } = mappingRequest;
  if (id) {
    const body = {
      type: ACTION_TYPES.REMOVE_MAPPING,
      payload: axiosInstance.delete(requestUrl + "/" + id),
      meta: { uiLocalUuid }
    };
    handleRequest(dispatch, body,
      getIntl().formatMessage({ id: 'ETL_MAPPING_DELETE_SUCCESS', defaultMessage: Default.MAPPING_DELETE_SUCCESS }),
      getIntl().formatMessage({ id: 'ETL_MAPPING_DELETE_FAILURE', defaultMessage: Default.MAPPING_DELETE_FAILURE }));
  } else {
    dispatch({
      type: ACTION_TYPES.REMOVE_LOCAL_MAPPING,
      meta: { uiLocalUuid }
    });
  }
};

export const getSources = () => async (dispatch) => {
  const requestUrl = 'ws/etllite/configs';
  await dispatch({
    type: ACTION_TYPES.FETCH_SOURCES,
    payload: axiosInstance.get(requestUrl)
  });
};

export const testMapping = (mappingId) => async (dispatch) => {
  const requestUrl = `ws/etllite/mappings/${mappingId}/test`;
  const body = {
    type: ACTION_TYPES.TEST_MAPPING,
    payload: axiosInstance.get(requestUrl),
    meta: mappingId
  };

  handleRequest(dispatch, body,
    getIntl().formatMessage({ id: 'ETL_MAPPING_TEST_SUCCESS', defaultMessage: Default.MAPPING_TEST_SUCCESS }),
    getIntl().formatMessage({ id: 'ETL_MAPPING_TEST_FAILURE', defaultMessage: Default.MAPPING_TEST_FAILURE }));
};

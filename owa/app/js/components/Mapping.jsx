/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import {
  Form,
  Button,
  FormGroup,
  FormControl
} from 'react-bootstrap';
import * as Yup from "yup";
import { validateForm } from '../utils/validation-util';
import ErrorDesc from './ErrorDesc';
import TextLabel from './TextLabel';
import * as Default from '../utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import Tooltip from './Tooltip';
import MappingTestResult from './MappingTestResult';

const Mapping = (props) => {

  const validationSchema = Yup.object().shape({
    name: Yup.string()
      .required(getIntl().formatMessage({ id: 'ETL_FIELD_REQUIRED', defaultMessage: Default.FIELD_REQUIRED })),
    source: Yup.string()
      .required(getIntl().formatMessage({ id: 'ETL_FIELD_REQUIRED', defaultMessage: Default.FIELD_REQUIRED }))
  });

  const handleChange = (event) => {
    let fieldName = event.target.name;
    let fieldVal = event.target.value;
    props.mapping[fieldName] = fieldVal;
    props.mapping.isTestable = false;
    props.onChange(props.mapping);
    
    validateForm(props.mapping, validationSchema)
      .then(() => {
        props.mapping.errors = null;
        props.onChange(props.mapping);

      })
      .catch((errors) => {
        props.mapping.errors = errors;
        props.onChange(props.mapping);
      });
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    validateForm(props.mapping, validationSchema)
      .then(() => {
        event.preventDefault();
        if (props.mapping.id) {
          props.updateMapping(props.mapping);
        } else {
          props.createMapping(props.mapping);
        }
      })
      .catch((errors) => {
        props.mapping.errors = errors;
        props.onChange(props.mapping);
      });
  };

  const handleDelete = (event) => {
    event.preventDefault();
    props.removeMapping(props.mapping);
  }

  const handleTest = (event) => {
    event.preventDefault();
    if (props.mapping.isTestable) {
      props.testMapping(props.mapping.id);
    }
  }

  const renderTestButton = () => {
    if (props.mapping.isTestable) {
      return (
        <Button className="btn btn-primary btn-md pull-right test-btn"
          onClick={handleTest}
          title={getIntl().formatMessage({ id: 'ETL_MAPPING_TEST_BUTTON_TOOLTIP_ENABLED', defaultMessage: Default.MAPPING_TEST_BUTTON_TOOLTIP_ENABLED })}>
          Test
        </Button>
      );
    }
    return (
      <Button className="btn btn-primary btn-md pull-right disabled test-btn"
        title={getIntl().formatMessage({ id: 'ETL_MAPPING_TEST_BUTTON_TOOLTIP_DISABLED', defaultMessage: Default.MAPPING_TEST_BUTTON_TOOLTIP_DISABLED })}>
        Test
      </Button>
    );
  }

  const formClass = 'form-control';

  const errorFormClass = formClass + ' error-field';

  const { errors } = props.mapping;

  return (
    <div>
      <Form className="form" onSubmit={handleSubmit}>
        <FormGroup controlId={"formName" + props.mapping.uiLocalUuid}>
          <TextLabel text={getIntl().formatMessage({ id: 'ETL_MAPPING_NAME_LABEL', defaultMessage: Default.MAPPING_NAME_LABEL })} isMandatory={true} isWithColon={true} />
          <Tooltip message={getIntl().formatMessage({ id: 'ETL_MAPPING_NAME_DESC', defaultMessage: Default.MAPPING_NAME_DESC })} />
          <FormControl type="text"
            name='name'
            value={props.mapping.name}
            onChange={handleChange}
            className={errors && errors.name ? errorFormClass : formClass} />
          {errors && <ErrorDesc field={errors.name} />}
        </FormGroup>
        <FormGroup controlId={"formSource" + props.mapping.uiLocalUuid}>
          <TextLabel text={getIntl().formatMessage({ id: 'ETL_MAPPING_SOURCE_LABEL', defaultMessage: Default.MAPPING_SOURCE_LABEL })} isMandatory={true} isWithColon={true} />
          <Tooltip message={getIntl().formatMessage({ id: 'ETL_MAPPING_SOURCE_DESC', defaultMessage: Default.MAPPING_SOURCE_DESC })} />
          <FormControl componentClass="select" name='source'
            value={props.mapping.source}
            onChange={handleChange}
            className={errors && errors.source ? errorFormClass : formClass}>
            <option value=""> -- select a source -- </option>
            {props.sources.map(item => {
              return (<option value={item} key={item}>{item}</option>);
            })}
          </FormControl>
          {errors && <ErrorDesc field={errors.source} />}
        </FormGroup>
        <FormGroup controlId={"formFetchSize" + props.mapping.uiLocalUuid}>
          <TextLabel text={getIntl().formatMessage({ id: 'ETL_MAPPING_FETCH_SIZE_LABEL', defaultMessage: Default.MAPPING_FETCH_SIZE_LABEL })} isMandatory={false} isWithColon={true} />
          <Tooltip message={getIntl().formatMessage({ id: 'ETL_MAPPING_FETCH_SIZE_DESC', defaultMessage: Default.MAPPING_FETCH_SIZE_DESC })} />
          <FormControl type="text"
            name='fetchSize'
            value={props.mapping.fetchSize}
            onChange={handleChange} />
        </FormGroup>
        <FormGroup controlId={"formQuery" + props.mapping.uiLocalUuid}>
          <TextLabel text={getIntl().formatMessage({ id: 'ETL_MAPPING_QUERY_LABEL', defaultMessage: Default.MAPPING_QUERY_LABEL })} isMandatory={false} isWithColon={true} />
          <Tooltip message={getIntl().formatMessage({ id: 'ETL_MAPPING_QUERY_DESC', defaultMessage: Default.MAPPING_QUERY_DESC })} />
          <FormControl componentClass="textarea"
            name='query'
            value={props.mapping.query}
            onChange={handleChange} />
        </FormGroup>
        <FormGroup controlId={"formTransform" + props.mapping.uiLocalUuid}>
          <TextLabel text={getIntl().formatMessage({ id: 'ETL_MAPPING_TRANSFORM_LABEL', defaultMessage: Default.MAPPING_TRANSFORM_LABEL })} isMandatory={false} isWithColon={true} />
          <Tooltip message={getIntl().formatMessage({ id: 'ETL_MAPPING_TRANSFORM_DESC', defaultMessage: Default.MAPPING_TRANSFORM_DESC })} />
          <FormControl componentClass="textarea"
            name='transformTemplate'
            value={props.mapping.transformTemplate}
            onChange={handleChange} />
        </FormGroup>
        <FormGroup controlId={"formLoad" + props.mapping.uiLocalUuid}>
          <TextLabel text={getIntl().formatMessage({ id: 'ETL_MAPPING_LOAD_LABEL', defaultMessage: Default.MAPPING_LOAD_LABEL })} isMandatory={false} isWithColon={true} />
          <Tooltip message={getIntl().formatMessage({ id: 'ETL_MAPPING_LOAD_DESC', defaultMessage: Default.MAPPING_LOAD_DESC })} />
          <FormControl componentClass="textarea"
            name='loadTemplate'
            value={props.mapping.loadTemplate}
            onChange={handleChange} />
        </FormGroup>
        <FormGroup controlId={"formCRON" + props.mapping.uiLocalUuid}>
          <TextLabel text={getIntl().formatMessage({ id: 'ETL_MAPPING_CRON_LABEL', defaultMessage: Default.MAPPING_CRON_LABEL })} isMandatory={false} isWithColon={true} />
          <Tooltip message={getIntl().formatMessage({ id: 'ETL_MAPPING_CRON_DESC', defaultMessage: Default.MAPPING_CRON_DESC })} />
          <FormControl type="text"
            name='cronExpression'
            value={props.mapping.cronExpression}
            onChange={handleChange} />
        </FormGroup>
        <FormGroup controlId={"formTestResultsSize" + props.mapping.uiLocalUuid}>
          <TextLabel text={getIntl().formatMessage({ id: 'ETL_MAPPING_TEST_RESULTS_SIZE_LABEL', defaultMessage: Default.MAPPING_TEST_RESULTS_SIZE_LABEL })} isMandatory={false} isWithColon={true} />
          <Tooltip message={getIntl().formatMessage({ id: 'ETL_MAPPING_TEST_RESULTS_SIZE_DESC', defaultMessage: Default.MAPPING_TEST_RESULTS_SIZE_DESC })} />
          <FormControl type="text"
            name='testResultsSize'
            value={props.mapping.testResultsSize}
            onChange={handleChange} />
        </FormGroup>
        <Button className="btn btn-success btn-md confirm" type="submit">Save</Button>
        <Button className="btn btn-danger btn-md" onClick={handleDelete}>Delete</Button>
        {renderTestButton()}
      </Form>
      <div>
        {props.mapping.isTestable && <MappingTestResult data={props.mapping.testResults} />}
      </div>
    </div>
  );
};

export default Mapping;

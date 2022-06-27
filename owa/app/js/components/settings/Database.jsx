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
  Button,
  FormGroup,
  Row,
  Col,
  Badge
} from 'react-bootstrap';
import PropTypes from 'prop-types';
import { Accordion } from '@openmrs/react-components';
import { DatabaseModel } from '../../shared/model/database.model';
import DatabaseField from './DatabaseField';
import RemoveButton from './RemoveButton';
import './settings.scss';
import { DBConnectionType } from '../../shared/model/DBConnectionType';
import { validateField } from '../../utils/validation-util';

const Database = props => {

  const inputMd = 5;
  const fullLineMd = 10;

  const handleChange = event => {
    let fieldName = event.target.name;
    let fieldVal = event.target.value;
    props.database[fieldName] = fieldVal;
    const eventCopy = { target: { name: fieldName, value: fieldVal } };
    props.handleChange(eventCopy, props.database.localId);

    validateField(props.database, fieldName, props.validationSchema(props.database))
      .then(() => {
        if (props.database.errors) {
          delete props.database.errors[fieldName];
        }
        props.handleChange(eventCopy, props.database.localId);
      })
      .catch((errors) => {
        if (props.database.errors) {
          props.database.errors[fieldName] = errors[fieldName];
        } else {
          props.database.errors = errors;
        }
        props.handleChange(eventCopy, props.database.localId);
      });
  };

  const formClass = 'form-control';
  const errorFormClass = formClass + ' error-field';
  const { errors } = props.database;

  return (
    <Row className="database-container">
      <Col md={11}>
        <Accordion title={props.database.name} border open>
          <FormGroup>
            <Row className="db-field-row">
              <DatabaseField {...{ handleChange }}
                inputMd={fullLineMd}
                className={errors && errors.name ? errorFormClass : formClass}
                disabled={props.database.confirmed}
                label="Name"
                tooltip="This is the name of the source system. e. g ICEA."
                value={props.database.name}
                error={errors && errors.name}
              />
            </Row>
          </FormGroup>
          <FormGroup>
            <Row className="db-field-row">
              <DatabaseField {...{ inputMd, handleChange }}
                className={errors && errors.type ? errorFormClass : formClass}
                label="Type"
                componentClass="select"
                value={props.database.type}
                error={errors && errors.type}
              >
                <React.Fragment>
                  <option hidden value="">Select</option>
                  {DBConnectionType.map(v => <option key={v.value} value={v.value}>{v.value}</option>)}
                </React.Fragment>
              </DatabaseField>
              <Col md={inputMd} />
            </Row>
          </FormGroup>
          <FormGroup>
            <Row className="db-field-row">
              <DatabaseField {...{ handleChange }}
                className={errors && errors.url ? errorFormClass : formClass}
                inputMd={fullLineMd}
                label="JDBC URL"
                placeholder="URL"
                name="url"
                tooltip="This is the JDBC URL of source systems database."
                value={props.database.url}
                error={errors && errors.url}
              />
            </Row>
          </FormGroup>
          <FormGroup>
            <Row className="db-field-row">
              <DatabaseField {...{ inputMd, handleChange }}
                className={errors && errors.user ? errorFormClass : formClass}
                label="User"
                tooltip="This is the user name of Source systems database."
                value={props.database.user}
                error={errors && errors.user}
              />
              <DatabaseField {...{ inputMd, handleChange }}
                className={errors && errors.dbPassword ? errorFormClass : formClass}
                label="Password"
                type="password"
                name="dbPassword"
                tooltip="This is the password of Source systems database."
                value={props.database.dbPassword}
                error={errors && errors.dbPassword}
              />
            </Row>
          </FormGroup>
          <FormGroup>
            <Row className="db-field-row">
              <DatabaseField {...{ handleChange }}
                label="Test Query"
                inputMd={fullLineMd}
                componentClass="textarea"
                placeholder="Query"
                name="query"
                tooltip="This is query to test the Source systems database."
                value={props.database.query}
                error={errors && errors.query}
              />
            </Row>
          </FormGroup>
          <Row className="db-field-row">
            <Col md={fullLineMd}>
              {props.database.confirmed &&
                <Button
                  className="btn btn-success btn-md sec-btn"
                  onClick={() => props.testDatabase(props.database)}>Test!
                </Button>}
              {props.database.testResult &&
                <Badge>
                  {props.database.testResult}
                </Badge>}
            </Col>
          </Row>
        </Accordion>
      </Col>
      <Col md={1}>
        <RemoveButton
          disabled={props.isLoading}
          handleRemove={() => props.removeDatabase(props.database)}
          tooltip="Delete!" />
      </Col>
    </Row>
  );
}

Database.propTypes = {
  isLoading: PropTypes.bool.isRequired,
  database: PropTypes.instanceOf(DatabaseModel).isRequired,
  handleChange: PropTypes.func.isRequired,
  removeDatabase: PropTypes.func.isRequired
}

export default Database;

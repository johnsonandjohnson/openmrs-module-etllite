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
  Button
} from 'react-bootstrap';
import PropTypes from 'prop-types';
import Database from './Database';
import { DatabaseModel } from '../../shared/model/database.model';

const Databases = (props) =>
  <React.Fragment>
    <h2>Databases</h2>
    <Button
      disabled={props.isLoading}
      className="btn btn-success btn-md add-btn"
      onClick={props.addDatabase}>
      <i className="fa fa-plus"></i> Add Database
    </Button>
    <Form className="form form-group" onSubmit={e => e.preventDefault()}>
      {props.databases.map((db, i) =>
        <Database
          isLoading={props.isLoading}
          handleChange={props.handleChange}
          removeDatabase={props.removeDatabase}
          testDatabase={props.testDatabase}
          validationSchema={props.validationSchema}
          key={i} database={db} />)}
    </Form>
  </React.Fragment>

Databases.propTypes = {
  isLoading: PropTypes.bool.isRequired,
  databases: PropTypes.arrayOf(PropTypes.instanceOf(DatabaseModel)).isRequired,
  handleChange: PropTypes.func.isRequired,
  addDatabase: PropTypes.func.isRequired,
  removeDatabase: PropTypes.func.isRequired,
  testDatabase: PropTypes.func.isRequired
}

export default Databases;

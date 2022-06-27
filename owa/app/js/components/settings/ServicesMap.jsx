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
  FormGroup,
  ControlLabel,
  FormControl
} from 'react-bootstrap';
import PropTypes from 'prop-types';

const ServicesMap = props =>
  <React.Fragment>
    <h2>Services Map</h2>
    <Form className="form" onSubmit={e => e.preventDefault()}>
      <FormGroup controlId={'settingsForm'}>
        <ControlLabel>Services:</ControlLabel>
        <FormControl type="text"
          componentClass="textarea"
          name="services"
          value={props.services}
          onChange={props.handleChange} />
      </FormGroup>
    </Form>
  </React.Fragment>

ServicesMap.propTypes = {
  services: PropTypes.string.isRequired,
  handleChange: PropTypes.func.isRequired
}

export default ServicesMap;

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
  FormControl,
  Col
} from 'react-bootstrap';
import PropTypes from 'prop-types';
import './settings.scss';
import Tooltip from '../tooltip';
import ErrorDesc from '../ErrorDesc';

const DatabaseField = props => {

  return (
    <Col md={props.inputMd}>
      <label>{props.label}:</label>
      <Tooltip message={props.tooltip} />
      <FormControl
        className={props.className}
        disabled={props.disabled}
        type={props.type}
        componentClass={props.componentClass}
        placeholder={props.placeholder ? props.placeholder : props.label}
        name={props.name ? props.name : props.label.toLowerCase()}
        value={props.value}
        onChange={props.handleChange} >
        {props.children}
      </FormControl>
      {props.error && <ErrorDesc field={props.error} />}
    </Col>
  );
}

DatabaseField.defaultProps = {
  type: "text",
  value: "",
  disabled: false
}

DatabaseField.propTypes = {
  inputMd: PropTypes.number.isRequired,
  handleChange: PropTypes.func.isRequired,
  value: PropTypes.string,
  tooltip: PropTypes.string,
  name: PropTypes.string,
  type: PropTypes.string,
  componentClass: PropTypes.string,
  placeholder: PropTypes.string,
  children: PropTypes.element,
  disabled: PropTypes.bool
}

export default DatabaseField;

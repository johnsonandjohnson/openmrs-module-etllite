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
import { ControlLabel } from 'react-bootstrap';
import * as Default from '../utils/messages'
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";

const renderMandatoryField = () => {
  return (
    <i> ({getIntl().formatMessage({ id: 'ETL_REQUIRED_LABEL', defaultMessage: Default.REQUIRED_LABEL })}) </i>
  )
}

const renderColon = () => {
  return (
    <span>:</span>
  )
}

const TextLabel = (props) => {
  const { text, isMandatory, isWithColon } = props;

  return (!!text && (
    <ControlLabel>
      {text}
      {isMandatory && renderMandatoryField()}
      {isWithColon && renderColon()}
    </ControlLabel>
  ));
};

export default TextLabel;


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
  Modal,
  Button
} from 'react-bootstrap';
import PropTypes from 'prop-types';
import _ from 'lodash';
import { getIntl } from '@openmrs/react-components/lib/components/localization/withLocalization';
import * as Default from '../utils/messages'

const OpenMRSModal = (props) => {
  return (
    <Modal show={props.show} onHide={props.handleClose}>
      <Modal.Header>
        <Modal.Title>{getTitleOrDefault(props)}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <p>{getDescriptionOrDefault(props)}</p>
        <Button bsClass="button confirm right" onClick={props.confirm}>{getConfirmLabelOrDefault(props)}</Button>
        <Button bsClass="button cancel" onClick={props.deny}>{getCancelLabelOrDefault(props)}</Button>
      </Modal.Body>
    </Modal>
  );
};

const getTitleOrDefault = (props) => {
  const { title } = props;
  return title ? title : getIntl().formatMessage({ id: 'ETL_MODAL_DEFAULT_TITLE', defaultMessage: Default.ETL_MODAL_DEFAULT_TITLE });
};

const getDescriptionOrDefault = (props) => {
  const { txt } = props;
  return txt ? txt : getIntl().formatMessage({ id: 'ETL_MODAL_DEFAULT_DESCRIPTION', defaultMessage: Default.ETL_MODAL_DEFAULT_DESCRIPTION });
};

const getConfirmLabelOrDefault = (props) => {
  const { confirmLabel } = props;
  return confirmLabel ? confirmLabel : getIntl().formatMessage({ id: 'ETL_MODAL_CONFIRM_LABEL', defaultMessage: Default.ETL_MODAL_CONFIRM_LABEL });
};

const getCancelLabelOrDefault = (props) => {
  const { cancelLabel } = props;
  return cancelLabel ? cancelLabel : getIntl().formatMessage({ id: 'ETL_MODAL_CANCEL_LABEL', defaultMessage: Default.ETL_MODAL_CANCEL_LABEL });
};

OpenMRSModal.propTypes = {
  title: PropTypes.string,
  txt: PropTypes.string,
  confirmLabel: PropTypes.string,
  cancelLabel: PropTypes.string,
  deny: PropTypes.func.isRequired,
  confirm: PropTypes.func.isRequired,
  show: PropTypes.bool.isRequired
};

export default OpenMRSModal;

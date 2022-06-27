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
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import { Button } from 'react-bootstrap';
import {
  getConfig,
  updateConfigLocally,
  removeDatabase,
  changeServices,
  addDatabase,
  changeDatabase,
  updateConfig,
  testDatabase,
  openModal,
  closeModal
} from '../../reducers/configReducer';
import ServicesMap from './ServicesMap';
import Databases from './Databases';
import { ConfigModel } from '../../shared/model/config.model';
import { errorToast } from '../../utils/toast-display-util';
import OpenMRSModal from '../OpenMRSModal';
import { validateForm } from '../../utils/validation-util';
import * as Default from '../../utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import * as Yup from "yup";
import * as Default from '../../utils/messages';

const FIELD_REQUIRED = getIntl().formatMessage({ id: 'ETL_FIELD_REQUIRED', defaultMessage: Default.FIELD_REQUIRED });

const validationSchema = db => Yup.object().shape({
  name: Yup
  .string()
    .trim()
    .required(FIELD_REQUIRED),
  user: Yup.string()
    .trim()
    .required(FIELD_REQUIRED),
  type: Yup.string()
    .trim()
    .required(FIELD_REQUIRED),
  url: Yup.string()
    .trim()
    .required(FIELD_REQUIRED),
  dbPassword: db.confirmed ? null : Yup.string()
    .trim()
    .required(FIELD_REQUIRED)
});

export class Settings extends React.Component {
  componentDidMount() {
    this.props.getConfig();
  }

  isValidationOk = databases => {
    let result = true;
    databases.forEach((db) => {
      if (db.errors) {
        result = false;
      } else {
      }
    });
    return result;
  }

  handleSave = () => {
    let configCopy = _.cloneDeep(this.props.config);
    let promises = configCopy.databases.map((db, i) =>
      validateForm(db, validationSchema(db))
        .then(() => {
          configCopy.databases[i].errors = null;
        })
        .catch((errors) => {
          configCopy.databases[i].errors = errors;
        })
    );
    Promise.all(promises).then(() => {
      if (this.isValidationOk(configCopy.databases)) {
        this.props.updateConfig(this.props.config);
      } else {
        this.props.updateConfigLocally(configCopy);
        errorToast(getIntl().formatMessage({ id: 'ETL_INVALID_CONFIGURATION', defaultMessage: Default.INVALID_CONFIGURATION }));
      }
    });
  };

  removeDatabase = (db) => {
    if (db.confirmed) {
      this.props.openModal(db.localId);
    } else {
      this.props.removeDatabase(this.props.config.databases, db.localId, true);
    }
  }

  handleClose = () => {
    this.props.closeModal();
  }

  handleConfirm = () => {
    this.props.removeDatabase(this.props.config.databases, this.props.idToDelete);
  }

  render() {
    const { config, changeServices, addDatabase, testDatabase, isLoading } = this.props;
    const handleChange = this.props.changeDatabase;

    return (
      <div className="body-wrapper">
        <OpenMRSModal
          deny={this.handleClose}
          confirm={this.handleConfirm}
          show={this.props.showModal}
          title={getIntl().formatMessage({ id: 'ETL_DELETE_DATABASE_TITLE', defaultMessage: Default.ETL_DELETE_DATABASE_TITLE })}
          txt={getIntl().formatMessage({ id: 'ETL_DELETE_DATABASE_DESCRIPTION', defaultMessage: Default.ETL_DELETE_DATABASE_DESCRIPTION })} />
        <ServicesMap services={config.services} handleChange={changeServices} />
        <Databases databases={config.databases} removeDatabase={this.removeDatabase}
          {... { addDatabase, testDatabase, handleChange, isLoading, validationSchema }} />
        <Button
          disabled={isLoading}
          className="btn btn-success btn-md confirm"
          onClick={this.handleSave}>Save
        </Button>
      </div>
    );
  }
}

Settings.propTypes = {
  isLoading: PropTypes.bool.isRequired,
  config: PropTypes.instanceOf(ConfigModel).isRequired,
  changeServices: PropTypes.func.isRequired,
  addDatabase: PropTypes.func.isRequired,
  getConfig: PropTypes.func.isRequired,
  updateConfigLocally: PropTypes.func.isRequired,
  removeDatabase: PropTypes.func.isRequired,
  changeDatabase: PropTypes.func.isRequired,
  updateConfig: PropTypes.func.isRequired,
  testDatabase: PropTypes.func.isRequired,
  openModal: PropTypes.func.isRequired,
  closeModal: PropTypes.func.isRequired
}

const mapStateToProps = state => ({
  isLoading: state.configReducer.isLoading,
  config: state.configReducer.config,
  showModal: state.configReducer.showModal,
  idToDelete: state.configReducer.idToDelete
});

const mapDispatchToProps = {
  getConfig,
  updateConfigLocally,
  removeDatabase,
  changeServices,
  addDatabase,
  changeDatabase,
  updateConfig,
  testDatabase,
  openModal,
  closeModal
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Settings);

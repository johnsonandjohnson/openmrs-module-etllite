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
import { Accordion } from '@openmrs/react-components';
import { Button } from 'react-bootstrap';
import {
  reset, getMappings, addNew, changeMapping,
  createMapping, updateMapping, removeMapping, openModal, closeModal,
  getSources, testMapping
} from '../reducers/mappingReducer';
import OpenMRSModal from './OpenMRSModal';
import Mapping from './Mapping';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import * as Default from '../utils/messages';
import DOMPurify from 'dompurify';

export class Mappings extends React.Component {

  constructor(props) {
    super(props);
    this.handleAdd = this.handleAdd.bind(this);
    this.newEntry = null;
  }

  handleAdd(e) {
    e.preventDefault();
    this.props.addNew();
  }

  componentDidMount() {
    this.props.getSources();
    this.props.getMappings();
    this.focusDiv();
  }

  componentDidUpdate = () => {
    this.focusDiv();
  }

  getOffsetTop(element) {
    let offsetTop = 0;
    while (element) {
      offsetTop += element.offsetTop;
      element = element.offsetParent;
    }
    return offsetTop;
  }

  focusDiv() {
    if (this.newEntry) {
      window.scrollTo({ left: 0, top: this.getOffsetTop(this.newEntry), behavior: 'smooth' });
      this.newEntry = null;
    }
  }

  handleClose = () => {
    this.props.closeModal();
  }

  handleConfirm = () => {
    this.props.removeMapping(this.props.mappingToDelete);
  }

  removeMapping = (mapping) => {
    if (mapping.id) {
      this.props.openModal(mapping);
    } else {
      this.props.removeMapping(mapping);
    }
  }

  render() {
    return (
      <div className="body-wrapper">
        <h1>
          {getIntl().formatMessage({ id: 'ETL_MAPPING_TITLE', defaultMessage: Default.ETL_MAPPING_TITLE })}
        </h1>
        <OpenMRSModal
          deny={this.handleClose}
          confirm={this.handleConfirm}
          show={this.props.showModal}
          title={getIntl().formatMessage({ id: 'ETL_DELETE_MAPPING_TITLE', defaultMessage: Default.ETL_DELETE_MAPPING_TITLE })}
          txt={getIntl().formatMessage({ id: 'ETL_DELETE_MAPPING_DESCRIPTION', defaultMessage: Default.ETL_DELETE_MAPPING_DESCRIPTION })} />
        <Button className="btn btn-success btn-md add-btn" onClick={this.handleAdd}><i className="fa fa-plus"></i> Add ETL Mapping</Button>

        {this.props.mappings.map(item => {
          return (
            <Accordion
              title={`${getIntl().formatMessage({
                id: 'ETL_MAPPING_NAME_PREFIX',
                defaultMessage: Default.ETL_MAPPING_NAME_PREFIX,
              })} 
              ${
                DOMPurify.sanitize(item.name) ??
                getIntl().formatMessage({ id: 'ETL_MAPPING_NOT_SAVED', defaultMessage: Default.ETL_MAPPING_NOT_SAVED })
              }`}
              border={true}
              open={DOMPurify.sanitize(item.isOpen)}
              key={DOMPurify.sanitize(item.uiLocalUuid)}
            >
              <div
                ref={(div) => {
                  if (DOMPurify.sanitize(item.isOpen)) {
                    item.isOpen = null;
                    this.newEntry = div;
                  }
                }}
              >
                <Mapping
                  mapping={item}
                  onChange={this.props.changeMapping}
                  sources={this.props.sources}
                  createMapping={this.props.createMapping}
                  updateMapping={this.props.updateMapping}
                  removeMapping={this.removeMapping}
                  testMapping={this.props.testMapping}
                  onTestableChange={this.props.changeTestable}
                />
              </div>
            </Accordion>
          );
        })}
      </div>
    );
  }
}

const mapStateToProps = state => ({
  mappings: state.mappingReducer.mappings,
  sources: state.mappingReducer.sources,
  showModal: state.mappingReducer.showModal,
  mappingToDelete: state.mappingReducer.mappingToDelete
});

const mapDispatchToProps = {
  reset,
  getMappings,
  addNew,
  changeMapping,
  createMapping,
  updateMapping,
  removeMapping,
  openModal,
  closeModal,
  testMapping,
  getSources
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Mappings);

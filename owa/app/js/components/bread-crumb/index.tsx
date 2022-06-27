/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React, { ReactFragment } from 'react';
import { connect } from 'react-redux';
import { Link, withRouter, RouteComponentProps } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { UnregisterCallback } from 'history';
import './bread-crumb.scss';
import * as Default from '../../utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";

const MAPPINGS_ROUTE = '/mappings';
const SETTINGS_ROUTE = '/settings';
const MODULE_ROUTE = '/';
const OMRS_ROUTE = '../../';
const SYSTEM_ADMINISTRATION_ROUTE = `${OMRS_ROUTE}coreapps/systemadministration/systemAdministration.page`;

interface IBreadCrumbProps extends DispatchProps, StateProps, RouteComponentProps {
};

interface IBreadCrumbState {
  current: string
};

class BreadCrumb extends React.PureComponent<IBreadCrumbProps, IBreadCrumbState> {
  unlisten: UnregisterCallback;

  constructor(props) {
    super(props);

    const { history } = this.props;
    this.state = {
      current: history.location.pathname.toLowerCase()
    };
  }

  componentDidMount = () => {
    const { history } = this.props;
    this.unlisten = history.listen((location) => {
      const current = location.pathname.toLowerCase();
      this.setState({ current });
    });
  }

  componentWillUnmount = () => {
    this.unlisten();
  }

  renderDelimiter = () => {
    return (
      <span className="breadcrumb-link-item breadcrumb-delimiter">
        <FontAwesomeIcon size="sm" icon={['fas', 'chevron-right']} />
      </span>);
  }

  renderHomeCrumb = () => {
    return (
      <a href={OMRS_ROUTE} className="breadcrumb-link-item home-crumb">
        <FontAwesomeIcon icon={['fas', 'home']} />
      </a>);
  }

  renderCrumb = (link: string, txt: string, isAbsolute?: boolean) => {
    if (isAbsolute) {
      return (
        <a href={link} className="breadcrumb-link-item" >{txt}</a>
      );
    } else {
      return <Link to={link} className="breadcrumb-link-item">{txt}</Link>;
    }
  }

  renderLastCrumb = (txt: string) => {
    return <span className="breadcrumb-last-item">{txt}</span>;
  }

  renderCrumbs = (elements: Array<ReactFragment>) => {
    const delimiter = this.renderDelimiter();

    return (
      <React.Fragment>
        {this.renderHomeCrumb()}
        {elements.map((e, i) =>
          <React.Fragment key={`crumb-${i}`}>
            {delimiter}
            {e}
          </React.Fragment>)}
      </React.Fragment>
    );
  }

  getCrumbs = (path: string): Array<ReactFragment> => {
    const mappingCrumbs = [
      this.renderCrumb(SYSTEM_ADMINISTRATION_ROUTE, getIntl().formatMessage({ id: 'ETL_SYSTEM_ADMINISTRATION_BREADCRUMB', defaultMessage: Default.SYSTEM_ADMINISTRATION_BREADCRUMB }), true),
      this.renderCrumb(MODULE_ROUTE, getIntl().formatMessage({ id: 'ETL_GENERAL_MODULE_BREADCRUMB', defaultMessage: Default.GENERAL_MODULE_BREADCRUMB })),
      this.renderLastCrumb(getIntl().formatMessage({ id: 'ETL_MAPPINGS_BREADCRUMB', defaultMessage: Default.MAPPINGS_BREADCRUMB }))
    ];

    const settingsCrumbs = [
      this.renderCrumb(SYSTEM_ADMINISTRATION_ROUTE, getIntl().formatMessage({ id: 'ETL_SYSTEM_ADMINISTRATION_BREADCRUMB', defaultMessage: Default.SYSTEM_ADMINISTRATION_BREADCRUMB }), true),
      this.renderCrumb(MODULE_ROUTE, getIntl().formatMessage({ id: 'ETL_GENERAL_MODULE_BREADCRUMB', defaultMessage: Default.GENERAL_MODULE_BREADCRUMB })),
      this.renderLastCrumb(getIntl().formatMessage({ id: 'ETL_SETTINGS_BREADCRUMB', defaultMessage: Default.SETTINGS_BREADCRUMB }))
    ];

    switch (path) {
      case MAPPINGS_ROUTE:
        return mappingCrumbs;
      case SETTINGS_ROUTE:
        return settingsCrumbs;
      default:
        return [
          this.renderCrumb(SYSTEM_ADMINISTRATION_ROUTE, getIntl().formatMessage({ id: 'ETL_SYSTEM_ADMINISTRATION_BREADCRUMB', defaultMessage: Default.SYSTEM_ADMINISTRATION_BREADCRUMB }), true),
          this.renderLastCrumb(getIntl().formatMessage({ id: 'ETL_GENERAL_MODULE_BREADCRUMB', defaultMessage: Default.GENERAL_MODULE_BREADCRUMB }))
        ];
    }
  }

  buildBreadCrumb = () => {
    const { current } = this.state;
    return (
      <div id="breadcrumbs" className="breadcrumb">
        {this.renderCrumbs(this.getCrumbs(current))}
      </div>
    );
  }

  render = () => {
    return this.buildBreadCrumb();
  }
}

const mapStateToProps = ({ }: any) => ({
});

const mapDispatchToProps = ({
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default withRouter(connect(
  mapStateToProps,
  mapDispatchToProps
)(BreadCrumb));

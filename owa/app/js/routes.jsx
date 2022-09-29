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
import { Switch } from 'react-router-dom';
import { Header } from '@openmrs/react-components';
import PrivateRoute from './components/private-route/private-route';

import App from './components/App'
import Mappings from './components/Mappings';
import Settings from './components/settings/Settings';
import BreadCrumb from './components/bread-crumb';
import {ETL_MAPPINGS_PRIVILEGE, ETL_SETTINGS_PRIVILEGE} from "./config/privileges";
import Customize from './components/customize/customize'
import { initializeLocalizationWrapper } from './components/localization-wrapper/localization-wrapper';
import messagesEN from "./translations/en.json";

initializeLocalizationWrapper({
  en: messagesEN,
});

export default (store) => (<div>
  <Customize />
  <Header />
  <BreadCrumb />
  <Switch>
    <PrivateRoute exact path="/mappings" component={Mappings} requiredPrivilege={ETL_MAPPINGS_PRIVILEGE}/>
    <PrivateRoute exact path="/settings" component={Settings} requiredPrivilege={ETL_SETTINGS_PRIVILEGE} />
    <PrivateRoute exact path="/" component={App} />
  </Switch>
</div>);

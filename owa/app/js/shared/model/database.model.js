/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import uuid from "uuid";

export class DatabaseModel {
  constructor(response) {
    this.localId = uuid.v4();
    this.user = response && response.user ? response.user : '';
    this.name = response && response.name ? response.name : '';
    this.type = response && response.type ? response.type : '';
    this.query = response && response.query ? response.query : 'SELECT 0;';
    this.url = response && response.url ? response.url : '';
    this.confirmed = !!response;
    this.testResult = null;
    this.errors = null;
  }
  
  toRequest() {
    return({
      user: this.user, 
      name: this.name,
      type: this.type,
      query: this.query,
      url: this.url,
      dbPassword: this.dbPassword
    });
  }
}

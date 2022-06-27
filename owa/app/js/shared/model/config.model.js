/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import { DatabaseModel } from './database.model';

 export class ConfigModel {
  constructor(response) {
    this.services = response && response.services ? response.services : '';
    this.databases = response && response.databases ? response.databases.map(r => new DatabaseModel(r)) : [];
  }

  toRequest() {
    return({
      services: this.services,
      databases: this.databases.map(db => db.toRequest())
    });
  }
}

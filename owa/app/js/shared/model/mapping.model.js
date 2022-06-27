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

export class MappingModel {
  constructor(mappingResponse, isOpen) {
    this.uiLocalUuid = uuid.v4();
    this.id = mappingResponse ? mappingResponse.id : null;
    this.source = mappingResponse && mappingResponse.source ? mappingResponse.source : '';
    this.loadTemplate = mappingResponse && mappingResponse.loadTemplate ? mappingResponse.loadTemplate : '';
    this.transformTemplate = mappingResponse && mappingResponse.transformTemplate ? mappingResponse.transformTemplate : '';
    this.name = mappingResponse && mappingResponse.name ? mappingResponse.name : '';
    this.cronExpression = mappingResponse && mappingResponse.cronExpression ? mappingResponse.cronExpression : '';
    this.query = mappingResponse && mappingResponse.query ? mappingResponse.query : '';
    this.fetchSize = mappingResponse && mappingResponse.fetchSize ? mappingResponse.fetchSize : 1000;
    this.testResultsSize = mappingResponse && mappingResponse.testResultsSize ? mappingResponse.testResultsSize : 10;
    this.isOpen = isOpen;
    this.isTestable = mappingResponse && mappingResponse.id;
  }

  toRequest() {
    return({
      source: this.source,
      loadTemplate: this.loadTemplate,
      transformTemplate: this.transformTemplate,
      name: this.name,
      cronExpression: this.cronExpression,
      query: this.query,
      fetchSize: this.fetchSize,
      testResultsSize: this.testResultsSize
    });
  }
}

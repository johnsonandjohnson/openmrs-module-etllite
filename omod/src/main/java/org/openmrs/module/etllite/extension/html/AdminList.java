/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.extension.html;

import org.openmrs.module.web.extension.AdministrationSectionExt;

import java.util.HashMap;
import java.util.Map;

/**
 * This class defines the links that will appear on the administration page under the "etllite.title"
 * heading. This extension is enabled by defining (uncommenting) it in the config.xml file.
 */
public class AdminList extends AdministrationSectionExt {

  /**
   * @see AdministrationSectionExt#getMediaType()
   */
  @Override
  public MEDIA_TYPE getMediaType() {
    return MEDIA_TYPE.html;
  }

  /**
   * @see AdministrationSectionExt#getTitle()
   */
  public String getTitle() {
    return "etllite.title";
  }

  /**
   * @see AdministrationSectionExt#getLinks()
   */
  public Map<String, String> getLinks() {
    Map<String, String> map = new HashMap<>();
    map.put("/ms/uiframework/resource/etllite/swagger/index.html", "etllite.refapp.title");
    return map;
  }
}

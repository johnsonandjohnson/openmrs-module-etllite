/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.etllite.api.service.impl;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.etllite.api.constants.Constants;
import org.openmrs.module.etllite.api.exception.ETLRuntimeException;
import org.openmrs.module.etllite.api.service.SettingsManagerService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.core.io.ByteArrayResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class SettingsManagerServiceImpl extends BaseOpenmrsService implements SettingsManagerService {

  @Override
  public void saveRawConfig(String configFileName, ByteArrayResource resource) {
    File destinationFile = getDestinationFile(configFileName);

    try (InputStream is = resource.getInputStream(); OutputStream fos = Files.newOutputStream(destinationFile.toPath())) {
      IOUtils.copy(is, fos);
    } catch (IOException e) {
      throw new ETLRuntimeException("Error saving file " + configFileName, e);
    }
  }

  @Override
  public InputStream getRawConfig(String configFileName) {
    InputStream is = null;
    try {
      File configurationFile = getDestinationFile(configFileName);
      if (configurationFile.exists()) {
        is = Files.newInputStream(configurationFile.toPath());
      }
    } catch (IOException e) {
      throw new ETLRuntimeException("Error loading file " + configFileName, e);
    }
    return is;
  }

  @Override
  public boolean configurationExist(String configurationFileName) {
    return getDestinationFile(configurationFileName).exists();
  }

  private File getDestinationFile(String filename) {
    File configFileFolder = OpenmrsUtil.getDirectoryInApplicationDataDirectory(Constants.CONFIG_DIR);
    return new File(configFileFolder, FilenameUtils.getName(filename));
  }
}

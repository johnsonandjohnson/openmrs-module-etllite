/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import * as Default from './messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import { toast } from "react-toastify";
import { CloseButton } from './toast-builder-util';
import { ToastStatusContent } from './toast-builder-util';

// Propagates request send action and displays response notification 
export const handleRequest = async (dispatch, body, successMessage, errorMessage) => {

  const CLOSE_DELAY = 5000;
  const TOAST_CLASS = 'toast-item';
  const toastId = toast(
    <ToastStatusContent message={getIntl().formatMessage({ id: 'ETL_GENERIC_PROCESSING', defaultMessage: Default.GENERIC_PROCESSING })}
      type="notice"/>, 
    {
      autoClose: false,
      closeButton: false,
      className: TOAST_CLASS,
      hideProgressBar: true
    }
  );
  try {
    await dispatch(body);
    toast.update(toastId, {
      render: <ToastStatusContent message={successMessage} type={toast.TYPE.SUCCESS}/>,
      autoClose: CLOSE_DELAY,
      closeButton: <CloseButton />,
      className: TOAST_CLASS,
      hideProgressBar: true
    });
  } catch(e) {
    try {
      toast.update(toastId, {
        render: <ToastStatusContent message={[errorMessage, e.response.data.message].join(" ")} type={toast.TYPE.ERROR}/>,
        autoClose: CLOSE_DELAY,
        closeButton: <CloseButton />,
        className: TOAST_CLASS,
        hideProgressBar: true
      });
    } catch(e) {
      toast.update(toastId, {
        render: <ToastStatusContent message={getIntl().formatMessage({ id: 'ETL_GENERIC_FAILURE', defaultMessage: Default.GENERIC_FAILURE })}
          type={toast.TYPE.ERROR}/>,
        autoClose: CLOSE_DELAY,
        closeButton: <CloseButton />,
        className: TOAST_CLASS,
        hideProgressBar: true
      });
    }
  }
}
package org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos;

import org.springframework.http.ResponseEntity;

public interface UserDeviceServiceResponseHandler {
  ResponseEntity<?> handle(UserDeviceServiceResponseOK response);

  ResponseEntity<?> handle(UserDeviceServiceResponseFailureException response);
  ResponseEntity<?> handle(UserDeviceServiceResponseFailureInternalError response);
  ResponseEntity<?> handle(UserDeviceServiceResponseFailureInvalidInputParameter response);
  ResponseEntity<?> handle(UserDeviceServiceResponseFailureNotFound response);
}

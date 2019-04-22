package org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos;

import org.springframework.http.ResponseEntity;

public class UserDeviceServiceResponseFailureInternalError extends UserDeviceServiceResponseFailure{

  public UserDeviceServiceResponseFailureInternalError(String message) {
    super(message);
  }

  @Override
  public ResponseEntity<?> accept(UserDeviceServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

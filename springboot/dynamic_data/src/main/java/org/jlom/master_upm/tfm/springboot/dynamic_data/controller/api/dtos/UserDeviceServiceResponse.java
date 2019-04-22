package org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos;

import org.springframework.http.ResponseEntity;

public interface UserDeviceServiceResponse {
  ResponseEntity<?> accept(UserDeviceServiceResponseHandler handler);
}

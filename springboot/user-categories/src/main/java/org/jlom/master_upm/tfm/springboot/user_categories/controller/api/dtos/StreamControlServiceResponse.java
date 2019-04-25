package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos;

import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.StreamControlReturnValue;
import org.springframework.http.ResponseEntity;

public interface StreamControlServiceResponse {
  ResponseEntity<?> accept(org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.StreamControlServiceResponseHandler handler);
  StreamControlReturnValue accept(IStreamControlServiceResponseHandlerRPC handler);
}

package org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos;

import org.jlom.master_upm.tfm.springboot.recommendations.view.api.dtos.StreamControlReturnValue;
import org.springframework.http.ResponseEntity;

public interface StreamControlServiceResponse {
  ResponseEntity<?> accept(StreamControlServiceResponseHandler handler);
  StreamControlReturnValue accept(IStreamControlServiceResponseHandlerRPC handler);
}

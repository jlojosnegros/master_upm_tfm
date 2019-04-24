package org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos;

import org.jlom.master_upm.tfm.springboot.stream_control.view.api.dtos.StreamControlReturnValue;
import org.springframework.http.ResponseEntity;

public interface StreamControlServiceResponse {
  ResponseEntity<?> accept(StreamControlServiceResponseHandler handler);
  StreamControlReturnValue accept(IStreamControlServiceResponseHandlerRPC handler);
}

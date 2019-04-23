package org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos;

import org.springframework.http.ResponseEntity;

public class StreamControlServiceResponseFailureInternalError extends StreamControlServiceResponseFailure {

  public StreamControlServiceResponseFailureInternalError(String message) {
    super(message);
  }

  @Override
  public ResponseEntity<?> accept(StreamControlServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

package org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos;

import org.jlom.master_upm.tfm.springboot.stream_control.view.api.dtos.StreamControlReturnValue;
import org.springframework.http.ResponseEntity;

public class StreamControlServiceResponseFailureInternalError extends StreamControlServiceResponseFailure {

  public StreamControlServiceResponseFailureInternalError(String message) {
    super(message);
  }

  @Override
  public ResponseEntity<?> accept(StreamControlServiceResponseHandler handler) {
    return handler.handle(this);
  }

  @Override
  public StreamControlReturnValue accept(IStreamControlServiceResponseHandlerRPC handler) {
    return handler.handle(this);
  }
}

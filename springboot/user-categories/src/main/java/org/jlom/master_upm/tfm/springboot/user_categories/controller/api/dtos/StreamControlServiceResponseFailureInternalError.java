package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos;

import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.StreamControlReturnValue;
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

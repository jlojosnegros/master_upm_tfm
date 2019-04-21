package org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos;

import org.springframework.http.ResponseEntity;

public class ContentServiceResponseFailureInternalError extends ContentServiceResponseFailure {
  public ContentServiceResponseFailureInternalError(String message) {
    super(message);
  }

  @Override
  public ResponseEntity<?> accept(ContentServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

package org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos;

import org.springframework.http.ResponseEntity;

public interface StreamControlServiceResponseHandler {
  ResponseEntity<?> handle(StreamControlServiceResponseOK response);

  ResponseEntity<?> handle(StreamControlServiceResponseFailureException response);
  ResponseEntity<?> handle(StreamControlServiceResponseFailureInternalError response);
  ResponseEntity<?> handle(StreamControlServiceResponseFailureInvalidInputParameter response);
  ResponseEntity<?> handle(StreamControlServiceResponseFailureNotFound response);
}

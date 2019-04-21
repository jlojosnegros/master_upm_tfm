package org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos;

import org.springframework.http.ResponseEntity;

public interface ContentServiceResponseHandler {

  ResponseEntity<?> handle(ContentServiceResponseOk response);

  ResponseEntity<?> handle(ContentServiceResponseFailureException response);
  ResponseEntity<?> handle(ContentServiceResponseFailureInternalError response);
  ResponseEntity<?> handle(ContentServiceResponseFailureInvalidInputParameter response);
  ResponseEntity<?> handle(ContentServiceResponseFailureNotFound response);
}

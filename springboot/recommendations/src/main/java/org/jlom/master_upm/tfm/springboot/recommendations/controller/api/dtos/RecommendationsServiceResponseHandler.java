package org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos;

import org.springframework.http.ResponseEntity;

public interface RecommendationsServiceResponseHandler {
  ResponseEntity<?> handle(RecommendationsServiceResponseOK response);

  ResponseEntity<?> handle(RecommendationsServiceResponseFailureException response);
  ResponseEntity<?> handle(RecommendationsServiceResponseFailureInternalError response);
  ResponseEntity<?> handle(RecommendationsServiceResponseFailureInvalidInputParameter response);
  ResponseEntity<?> handle(RecommendationsServiceResponseFailureNotFound response);
}

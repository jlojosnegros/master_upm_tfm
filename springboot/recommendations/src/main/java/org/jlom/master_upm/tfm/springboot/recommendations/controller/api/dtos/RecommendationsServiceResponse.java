package org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos;

import org.springframework.http.ResponseEntity;

public interface RecommendationsServiceResponse {
  ResponseEntity<?> accept(RecommendationsServiceResponseHandler handler);
}

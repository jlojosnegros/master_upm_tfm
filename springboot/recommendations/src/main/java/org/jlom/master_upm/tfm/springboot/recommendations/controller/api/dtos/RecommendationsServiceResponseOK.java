package org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

@ToString
@EqualsAndHashCode
public class RecommendationsServiceResponseOK implements RecommendationsServiceResponse {

  public RecommendationsServiceResponseOK() {
  }

  @Override
  public ResponseEntity<?> accept(RecommendationsServiceResponseHandler handler) {
    return handler.handle(this);
  }

}

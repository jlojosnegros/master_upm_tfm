package org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos;

import org.springframework.http.ResponseEntity;

public class RecommendationsServiceResponseFailureInternalError extends RecommendationsServiceResponseFailure {

  public RecommendationsServiceResponseFailureInternalError(String message) {
    super(message);
  }

  @Override
  public ResponseEntity<?> accept(RecommendationsServiceResponseHandler handler) {
    return handler.handle(this);
  }

  @Override
  public StreamControlReturnValue accept(IStreamControlServiceResponseHandlerRPC handler) {
    return handler.handle(this);
  }
}

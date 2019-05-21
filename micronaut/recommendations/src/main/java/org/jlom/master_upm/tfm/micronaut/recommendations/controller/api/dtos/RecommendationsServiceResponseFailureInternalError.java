package org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.dtos;


import io.micronaut.http.HttpResponse;

public class RecommendationsServiceResponseFailureInternalError extends RecommendationsServiceResponseFailure {

  public RecommendationsServiceResponseFailureInternalError(String message) {
    super(message);
  }

  @Override
  public HttpResponse<?> accept(RecommendationsServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

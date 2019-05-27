package org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class RecommendationsServiceResponseOK implements RecommendationsServiceResponse {

  public RecommendationsServiceResponseOK() {
  }

  @Override
  public HttpResponse<?> accept(RecommendationsServiceResponseHandler handler) {
    return handler.handle(this);
  }

}

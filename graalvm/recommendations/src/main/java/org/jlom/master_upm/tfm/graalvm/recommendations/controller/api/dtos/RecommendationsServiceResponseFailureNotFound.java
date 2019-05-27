package org.jlom.master_upm.tfm.graalvm.recommendations.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RecommendationsServiceResponseFailureNotFound extends RecommendationsServiceResponseFailure {

  private final String paramName;
  private final Object value;

  public RecommendationsServiceResponseFailureNotFound(String message, String paramName, Object value) {
    super(message + " element not found for " + paramName + "=" + value.toString());
    this.paramName = paramName;
    this.value = value;
  }

  public RecommendationsServiceResponseFailureNotFound(String paramName, Object value) {
    this("error:", paramName , value);
  }

  @Override
  public HttpResponse<?> accept(RecommendationsServiceResponseHandler handler) {
    return handler.handle(this);
  }


}
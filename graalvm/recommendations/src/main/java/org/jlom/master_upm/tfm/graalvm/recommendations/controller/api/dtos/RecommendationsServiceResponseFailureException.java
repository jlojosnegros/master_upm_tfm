package org.jlom.master_upm.tfm.graalvm.recommendations.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RecommendationsServiceResponseFailureException extends RecommendationsServiceResponseFailure {

  private final Exception exception;

  public RecommendationsServiceResponseFailureException(String message, Exception exception) {
    super(message + " Exception:" + exception.getMessage());
    this.exception = exception;
  }

  public RecommendationsServiceResponseFailureException(Exception exception) {
    this("error: Exception captured. ", exception);
  }

  @Override
  public HttpResponse<?> accept(RecommendationsServiceResponseHandler handler) {
    return handler.handle(this);
  }
}
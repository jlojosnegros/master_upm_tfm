package org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

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
  public ResponseEntity<?> accept(RecommendationsServiceResponseHandler handler) {
    return handler.handle(this);
  }


}
package org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RecommendationsServiceResponseFailureInvalidInputParameter extends RecommendationsServiceResponseFailure {

  private final String paramName;
  private final Object paramValue;

  public RecommendationsServiceResponseFailureInvalidInputParameter(String message, String paramName, Object paramValue) {
    super(message + ":Parameter="+paramName+" with value="+paramValue.toString());
    this.paramName = paramName;
    this.paramValue = paramValue;
  }

  public RecommendationsServiceResponseFailureInvalidInputParameter(String paramName, Object param) {
    this ("InvalidInputParameter:",paramName,param);
  }

  @Override
  public ResponseEntity<?> accept(RecommendationsServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

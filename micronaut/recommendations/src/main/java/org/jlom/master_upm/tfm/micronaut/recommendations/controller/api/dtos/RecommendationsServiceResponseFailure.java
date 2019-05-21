package org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public abstract class RecommendationsServiceResponseFailure implements RecommendationsServiceResponse {

  private final String message;


  public RecommendationsServiceResponseFailure(String message) {
    this.message = message;
  }

}

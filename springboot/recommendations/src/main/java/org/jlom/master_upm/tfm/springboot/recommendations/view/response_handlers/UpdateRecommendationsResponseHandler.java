package org.jlom.master_upm.tfm.springboot.recommendations.view.response_handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.RecommendationsServiceResponseFailureException;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.RecommendationsServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.RecommendationsServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.RecommendationsServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.RecommendationsServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.RecommendationsServiceResponseHandler;
import org.jlom.master_upm.tfm.springboot.recommendations.view.exceptions.InvalidParamException;
import org.jlom.master_upm.tfm.springboot.recommendations.view.exceptions.WrapperException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.jlom.master_upm.tfm.springboot.recommendations.utils.DtosTransformations.serviceToView;
import static org.jlom.master_upm.tfm.springboot.recommendations.utils.JsonUtils.ObjectToJson;

public class UpdateRecommendationsResponseHandler implements RecommendationsServiceResponseHandler {
  private HttpServletRequest request;

  public UpdateRecommendationsResponseHandler(HttpServletRequest request) {
    this.request = request;
  }

  @Override
  public ResponseEntity<?> handle(RecommendationsServiceResponseOK response) {
    try {
      String json = ObjectToJson(serviceToView(response.getStreamControlData()));
      return new ResponseEntity<>(json,new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + response.getStreamControlData(), e);
    }
  }

  @Override
  public ResponseEntity<?> handle(RecommendationsServiceResponseFailureException response) {
    return null;
  }

  @Override
  public ResponseEntity<?> handle(RecommendationsServiceResponseFailureInternalError response) {
    return null;
  }

  @Override
  public ResponseEntity<?> handle(RecommendationsServiceResponseFailureInvalidInputParameter response) {
    return null;
  }

  @Override
  public ResponseEntity<?> handle(RecommendationsServiceResponseFailureNotFound response) {
    throw new InvalidParamException(response.getParamName(), response.getMessage());
  }
}

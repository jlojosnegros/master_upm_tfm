package org.jlom.master_upm.tfm.springboot.catalog.view.serivceresponsehandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseFailureException;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseHandler;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseOk;
import org.jlom.master_upm.tfm.springboot.catalog.view.exceptions.InvalidParamException;
import org.jlom.master_upm.tfm.springboot.catalog.view.exceptions.WrapperException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.jlom.master_upm.tfm.springboot.catalog.utils.JsonUtils.ObjectToJson;

public abstract class ServiceResponseHandler implements ContentServiceResponseHandler {

  private final HttpServletRequest request;

  public ServiceResponseHandler(HttpServletRequest request) {
    this.request = request;
  }

  protected HttpServletRequest getRequest() { return this.request;}

  protected ResponseEntity<?> handleResponseOk(ContentServiceResponseOk response, HttpStatus status) {
    try {
      return new ResponseEntity<>(ObjectToJson(response.getContent()),new HttpHeaders(), status);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + response.getContent(), e);
    }
  }

  @Override
  public ResponseEntity<?> handle(ContentServiceResponseFailureException response) {
    throw new WrapperException(response.getException());
  }

  @Override
  public ResponseEntity<?> handle(ContentServiceResponseFailureInternalError response) {
    throw  new WrapperException("Internal server error:" + response.getMessage());
  }

  @Override
  public ResponseEntity<?> handle(ContentServiceResponseFailureInvalidInputParameter response) {
    throw  new InvalidParamException(response.getParamName(), response.getMessage());
  }

}

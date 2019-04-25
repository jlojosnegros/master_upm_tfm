package org.jlom.master_upm.tfm.springboot.user_categories.view.response_handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureException;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseHandler;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.user_categories.view.exceptions.WrapperException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.jlom.master_upm.tfm.springboot.user_categories.utils.DtosTransformations.serviceToView;
import static org.jlom.master_upm.tfm.springboot.user_categories.utils.JsonUtils.ObjectToJson;

public class CreateUserCategoriesResponseHandler implements UserCategoriesServiceResponseHandler {
  private HttpServletRequest request;

  public CreateUserCategoriesResponseHandler(HttpServletRequest request) {

    this.request = request;
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseOK response) {
    try {
      String json = ObjectToJson(serviceToView(response.getStreamControlData()));
      return new ResponseEntity<>(json,new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + response.getStreamControlData(), e);
    }

  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseFailureException response) {
    return null;
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseFailureInternalError response) {
    return null;
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseFailureInvalidInputParameter response) {
    return null;
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseFailureNotFound response) {
    return null;
  }
}

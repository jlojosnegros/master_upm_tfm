package org.jlom.master_upm.tfm.springboot.user_categories.view.response_handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureException;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseHandler;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKContentPackage;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKUserCategory;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKUserData;
import org.jlom.master_upm.tfm.springboot.user_categories.view.exceptions.InvalidParamException;
import org.jlom.master_upm.tfm.springboot.user_categories.view.exceptions.WrapperException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.jlom.master_upm.tfm.springboot.user_categories.utils.DtosTransformations.serviceToView;
import static org.jlom.master_upm.tfm.springboot.user_categories.utils.JsonUtils.ObjectToJson;

public class UpdateUserCategoriesResponseHandler implements UserCategoriesServiceResponseHandler {
  private HttpServletRequest request;

  public UpdateUserCategoriesResponseHandler(HttpServletRequest request) {
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
  public ResponseEntity<?> handle(UserCategoriesServiceResponseOKUserData response) {
    try {
      String json = ObjectToJson(serviceToView(response.getUserData()));
      return new ResponseEntity<>(json,new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + response.getUserData(), e);
    }
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseOKContentPackage response) {
    return null;
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseOKUserCategory response) {
    return null;
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
    throw new InvalidParamException(response.getParamName(), response.getMessage());
  }
}

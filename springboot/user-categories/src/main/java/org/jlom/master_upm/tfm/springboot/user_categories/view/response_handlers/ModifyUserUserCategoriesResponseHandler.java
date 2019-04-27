package org.jlom.master_upm.tfm.springboot.user_categories.view.response_handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureException;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseHandler;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKCatalogContent;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKContentPackage;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKUserCategory;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKUserData;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.UserData;
import org.jlom.master_upm.tfm.springboot.user_categories.utils.DtosTransformations;
import org.jlom.master_upm.tfm.springboot.user_categories.utils.JsonUtils;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputUserCategoryData;
import org.jlom.master_upm.tfm.springboot.user_categories.view.exceptions.InvalidParamException;
import org.jlom.master_upm.tfm.springboot.user_categories.view.exceptions.WrapperException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.jlom.master_upm.tfm.springboot.user_categories.utils.DtosTransformations.serviceToView;
import static org.jlom.master_upm.tfm.springboot.user_categories.utils.JsonUtils.ObjectToJson;

public class ModifyUserUserCategoriesResponseHandler implements UserCategoriesServiceResponseHandler {
  private final HttpServletRequest request;

  public ModifyUserUserCategoriesResponseHandler(HttpServletRequest request) {
    this.request = request;
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseOKUserData response) {

    UserData userData = response.getUserData();
    InputUserCategoryData inputUserCategoryData = serviceToView(userData);
    try {
      String json = ObjectToJson(inputUserCategoryData);
      return new ResponseEntity<>(json,new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + inputUserCategoryData, e);
    }
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseOKContentPackage response) {
    throw new WrapperException("Internal server error:" + response);
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseOKUserCategory response) {
    throw new WrapperException("Internal server error:" + response);
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseOKCatalogContent response) {
    throw new WrapperException("Internal server error:" + response);
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseFailureException response) {
    throw new WrapperException(response.getException());
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseFailureInternalError response) {
    throw  new WrapperException("Internal server error:" + response.getMessage());
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseFailureInvalidInputParameter response) {
    throw  new InvalidParamException(response.getParamName(), response.getMessage());
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseFailureNotFound response) {
    throw new InvalidParamException(response.getParamName(), response.getMessage());
  }
}

package org.jlom.master_upm.tfm.springboot.user_categories.view.response_handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.CatalogContent;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureException;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseHandler;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKCatalogContent;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKContentPackage;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKUserCategory;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKUserData;
import org.jlom.master_upm.tfm.springboot.user_categories.utils.DtosTransformations;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.springboot.user_categories.view.exceptions.InvalidParamException;
import org.jlom.master_upm.tfm.springboot.user_categories.view.exceptions.WrapperException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jlom.master_upm.tfm.springboot.user_categories.utils.JsonUtils.ObjectToJson;
import static org.jlom.master_upm.tfm.springboot.user_categories.utils.JsonUtils.listToJson;

public class FilterUserCategoriesResponseHandler implements UserCategoriesServiceResponseHandler {
  private final HttpServletRequest request;

  public FilterUserCategoriesResponseHandler(HttpServletRequest request) {
    this.request = request;
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseOKUserData response) {
    throw new  WrapperException("Internal server error:" + response);
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseOKContentPackage response) {
    throw new  WrapperException("Internal server error:" + response);
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseOKUserCategory response) {
    throw new  WrapperException("Internal server error:" + response);
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseOKCatalogContent response) {

    Set<CatalogContent> filteredContent = response.getFilteredContent();

    List<InputCatalogContent> collect = filteredContent.stream()
            .map(DtosTransformations::serviceToView)
            .collect(Collectors.toList());

    try {
      String json = listToJson(collect);
      return new ResponseEntity<>(json,new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + response.getFilteredContent(), e);
    }
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseFailureException response) {
    throw new WrapperException(response.getException());
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseFailureInternalError response) {
    throw new WrapperException("Internal error: " + response.getMessage());
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseFailureInvalidInputParameter response) {
    throw new InvalidParamException(response.getParamName()+"="+response.getParamValue(),
            response.getMessage());
  }

  @Override
  public ResponseEntity<?> handle(UserCategoriesServiceResponseFailureNotFound response) {
    throw new InvalidParamException(response.getParamName(),response.getMessage());
  }
}

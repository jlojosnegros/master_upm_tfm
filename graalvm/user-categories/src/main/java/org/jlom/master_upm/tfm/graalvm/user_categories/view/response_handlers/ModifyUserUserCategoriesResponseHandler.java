package org.jlom.master_upm.tfm.graalvm.user_categories.view.response_handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureException;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseHandler;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKCatalogContent;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKContentPackage;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKUserCategory;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKUserData;
import org.jlom.master_upm.tfm.graalvm.user_categories.model.daos.UserData;
import org.jlom.master_upm.tfm.graalvm.user_categories.view.api.dtos.InputUserCategoryData;
import org.jlom.master_upm.tfm.graalvm.user_categories.view.exceptions.InvalidParamException;
import org.jlom.master_upm.tfm.graalvm.user_categories.view.exceptions.WrapperException;


import static org.jlom.master_upm.tfm.graalvm.user_categories.utils.DtosTransformations.serviceToView;
import static org.jlom.master_upm.tfm.graalvm.user_categories.utils.JsonUtils.ObjectToJson;

public class ModifyUserUserCategoriesResponseHandler implements UserCategoriesServiceResponseHandler {
  private final HttpRequest request;

  public ModifyUserUserCategoriesResponseHandler(HttpRequest request) {
    this.request = request;
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseOKUserData response) {

    UserData userData = response.getUserData();
    InputUserCategoryData inputUserCategoryData = serviceToView(userData);
    try {
      String json = ObjectToJson(inputUserCategoryData);
      return HttpResponse.ok(json);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + inputUserCategoryData, e);
    }
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseOKContentPackage response) {
    throw new WrapperException("Internal server error:" + response);
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseOKUserCategory response) {
    throw new WrapperException("Internal server error:" + response);
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseOKCatalogContent response) {
    throw new WrapperException("Internal server error:" + response);
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseFailureException response) {
    throw new WrapperException(response.getException());
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseFailureInternalError response) {
    throw  new WrapperException("Internal server error:" + response.getMessage());
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseFailureInvalidInputParameter response) {
    throw  new InvalidParamException(response.getParamName(), response.getMessage());
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseFailureNotFound response) {
    throw new InvalidParamException(response.getParamName(), response.getMessage());
  }
}

package org.jlom.master_upm.tfm.graalvm.user_categories.view.response_handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.CatalogContent;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureException;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseHandler;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKCatalogContent;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKContentPackage;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKUserCategory;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKUserData;
import org.jlom.master_upm.tfm.graalvm.user_categories.utils.DtosTransformations;
import org.jlom.master_upm.tfm.graalvm.user_categories.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.graalvm.user_categories.view.exceptions.InvalidParamException;
import org.jlom.master_upm.tfm.graalvm.user_categories.view.exceptions.WrapperException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jlom.master_upm.tfm.graalvm.user_categories.utils.JsonUtils.listToJson;

public class FilterUserCategoriesResponseHandler implements UserCategoriesServiceResponseHandler {
  private final HttpRequest request;

  public FilterUserCategoriesResponseHandler(HttpRequest request) {
    this.request = request;
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseOKUserData response) {
    throw new WrapperException("Internal server error:" + response);
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseOKContentPackage response) {
    throw new  WrapperException("Internal server error:" + response);
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseOKUserCategory response) {
    throw new  WrapperException("Internal server error:" + response);
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseOKCatalogContent response) {

    Set<CatalogContent> filteredContent = response.getFilteredContent();

    List<InputCatalogContent> collect = filteredContent.stream()
            .map(DtosTransformations::serviceToView)
            .collect(Collectors.toList());

    try {
      String json = listToJson(collect);
      return HttpResponse.ok(json);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + response.getFilteredContent(), e);
    }
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseFailureException response) {
    throw new WrapperException(response.getException());
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseFailureInternalError response) {
    throw new WrapperException("Internal error: " + response.getMessage());
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseFailureInvalidInputParameter response) {
    throw new InvalidParamException(response.getParamName()+"="+response.getParamValue(),
            response.getMessage());
  }

  @Override
  public HttpResponse<?> handle(UserCategoriesServiceResponseFailureNotFound response) {
    throw new InvalidParamException(response.getParamName(),response.getMessage());
  }
}

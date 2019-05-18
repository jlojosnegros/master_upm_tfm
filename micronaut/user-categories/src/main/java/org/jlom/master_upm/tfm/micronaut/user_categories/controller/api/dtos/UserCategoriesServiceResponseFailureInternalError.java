package org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos;


import io.micronaut.http.HttpResponse;

public class UserCategoriesServiceResponseFailureInternalError extends UserCategoriesServiceResponseFailure {

  public UserCategoriesServiceResponseFailureInternalError(String message) {
    super(message);
  }

  @Override
  public HttpResponse<?> accept(UserCategoriesServiceResponseHandler handler) {
    return handler.handle(this);
  }


}

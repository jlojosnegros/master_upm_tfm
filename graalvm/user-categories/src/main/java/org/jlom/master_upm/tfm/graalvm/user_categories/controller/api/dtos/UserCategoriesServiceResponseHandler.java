package org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos;

import io.micronaut.http.HttpResponse;

public interface UserCategoriesServiceResponseHandler {
  HttpResponse<?> handle(UserCategoriesServiceResponseOKUserData response);
  HttpResponse<?> handle(UserCategoriesServiceResponseOKContentPackage response);
  HttpResponse<?> handle(UserCategoriesServiceResponseOKUserCategory response);
  HttpResponse<?> handle(UserCategoriesServiceResponseOKCatalogContent response);

  HttpResponse<?> handle(UserCategoriesServiceResponseFailureException response);
  HttpResponse<?> handle(UserCategoriesServiceResponseFailureInternalError response);
  HttpResponse<?> handle(UserCategoriesServiceResponseFailureInvalidInputParameter response);
  HttpResponse<?> handle(UserCategoriesServiceResponseFailureNotFound response);
}

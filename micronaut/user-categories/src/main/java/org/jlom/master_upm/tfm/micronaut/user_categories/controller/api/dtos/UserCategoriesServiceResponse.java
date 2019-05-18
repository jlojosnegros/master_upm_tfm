package org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos;

import io.micronaut.http.HttpResponse;

public interface UserCategoriesServiceResponse {
  HttpResponse<?> accept(UserCategoriesServiceResponseHandler handler);
}

package org.jlom.master_upm.tfm.graalvm.user_categories.view.api;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import org.jlom.master_upm.tfm.graalvm.user_categories.view.api.dtos.InputUserCategoryData;
import org.jlom.master_upm.tfm.graalvm.user_categories.view.api.dtos.InputUserContentFiltered;

import javax.validation.Valid;

public interface UserCategoriesCommandInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @Post("/filter")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> filter(HttpRequest request, @Valid @Body InputUserContentFiltered inputUserContentFiltered);

  @Post("/user")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> addUser(HttpRequest request, @Valid @Body InputUserCategoryData inputUserCategoryData);

  @Delete("/user/{userId}")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> removeUser(HttpRequest request, @Valid @PathVariable("userId") long userId);

  @Post("/user/category")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> changeCategory(HttpRequest request, @Valid @Body InputUserCategoryData inputUserCategoryData);

  @Post("/user/add-packages")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> addPackages(HttpRequest request, @Valid @Body InputUserCategoryData streamData);

  @Post("/user/remove-packages")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> removePackages(HttpRequest request, @Valid @Body InputUserCategoryData streamData);

}

package org.jlom.master_upm.tfm.graalvm.user_categories.view.api;


import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;

public interface UserCategoriesQueryInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";


  @Get("/user/{userId}/packages")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> listPackagesForUser(HttpRequest request, @PathVariable(value = "userId") long userId);

  @Get("/user/{userId}/category")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> getCategoryForUser(HttpRequest request, @PathVariable(value = "userId") long userId);

  @Get("/packages")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> listAllPackages(HttpRequest request);

  @Get("/categories")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> listAllCategories(HttpRequest request);
}

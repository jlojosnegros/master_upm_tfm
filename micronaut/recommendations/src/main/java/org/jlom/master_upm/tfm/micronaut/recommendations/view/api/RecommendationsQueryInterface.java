package org.jlom.master_upm.tfm.micronaut.recommendations.view.api;


import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;

public interface RecommendationsQueryInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @Get("/users/{userId}/{top}")
  @Produces( {MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> getRecommendationsForUser(HttpRequest request,
                                            @PathVariable("userId") long userId,
                                            @PathVariable("top") long top);

  @Get("/most-viewed/{top}")
  @Produces( {MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> getMostViewed(HttpRequest request,
                                  @PathVariable("top") long top);
}

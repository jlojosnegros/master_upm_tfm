package org.jlom.master_upm.tfm.micronaut.apigw.view.api;


import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;

public interface ApiGwQueryInterface {
  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @Get("/content/soon")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> listContentAvailableSoon(HttpRequest request);

  @Get("/content/most-viewed/{top}")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> listMostViewedContent(HttpRequest request,
                                          @PathVariable("top") long top);
  @Get("/content/most-viewed")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> listMostViewedContent(HttpRequest request);

  @Get("/user/{userId}/recommended/{top}")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> listRecommended(HttpRequest request,
                                    @PathVariable("userId") long userId,
                                    @PathVariable("top") long top);

  @Get("/user/{userId}/liked/{top}")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> listLiked(HttpRequest request,
                              @PathVariable("userId") long userId,
                              @PathVariable("top") long top);
}

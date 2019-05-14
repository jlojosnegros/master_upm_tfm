package org.jlom.master_upm.tfm.micronaut.catalog.view.api;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.validation.Validated;

@Validated
public interface CatalogQueryInterface {
  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @Get("/content")
  @Produces(value = {MediaType.APPLICATION_JSON,APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> getAllContent();

  @Get("/content/{contentId}")
  @Produces(value = {MediaType.APPLICATION_JSON,APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> getContentById(@PathVariable("contentId") long contentId);

  @Get("/content/exactlyWithTags")
  @Produces(value = {MediaType.APPLICATION_JSON,APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> getContentExactlyWithTags(@QueryValue(value = "tags") String tags);

  @Get("/content/after")
  @Produces(value = {MediaType.APPLICATION_JSON,APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> getContentAvailableAfter(@QueryValue(value = "date") long date);

  @Get("/content/stream/{streamId}")
  @Produces(value = {MediaType.APPLICATION_JSON,APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> getContentByStreamId(@PathVariable("streamId") long streamId);

  @Get("/content/status/{status}")
  @Produces(value = {MediaType.APPLICATION_JSON,APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> getContentByStatus(@PathVariable("status") String status);
}


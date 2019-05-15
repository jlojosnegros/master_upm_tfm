package org.jlom.master_upm.tfm.micronaut.catalog.view.api;


import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import org.jlom.master_upm.tfm.micronaut.catalog.model.ContentStatus;
import org.jlom.master_upm.tfm.micronaut.catalog.view.api.dtos.InputCatalogContent;

import javax.validation.Valid;

public interface CatalogCommandInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @Post("/content/newContent")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> createNewContent(HttpRequest<?> request, @Valid @Body InputCatalogContent content);

  @Delete("/content/delete/{contentId}")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> deleteContent(HttpRequest<?> request, @Valid @PathVariable(value = "contentId") long contentId);

  @Post("/content/changeStatus/{contentId}")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> changeStatus(HttpRequest<?> request,
                                 @Valid @PathVariable(value = "contentId") long contentId,
                                 @Valid @Body ContentStatus status);

  @Post("/content/addTags/{contentId}")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> addTags(HttpRequest<?> request,
                            @Valid @PathVariable(value = "contentId") long contentId,
                            @Valid @QueryValue(value = "newTags") String tags);
}

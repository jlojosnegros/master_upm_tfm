package org.jlom.master_upm.tfm.micronaut.apigw.view.api;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import org.jlom.master_upm.tfm.micronaut.apigw.view.api.dtos.InputCatalogContent;
import javax.validation.Valid;

public interface ApiGwCommandInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @Post("/stream/play")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> play(HttpRequest request, @Valid @Body InputCatalogContent content);
}

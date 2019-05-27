package org.jlom.master_upm.tfm.graalvm.stream_control.view.api;


import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import org.jlom.master_upm.tfm.graalvm.stream_control.view.api.dtos.InputStreamData;

import javax.validation.Valid;

public interface StreamControlCommandInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @Post("/play")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> play(HttpRequest request, @Valid @Body InputStreamData streamData);

  @Post("/stop")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> stop(HttpRequest request, @Valid @Body InputStreamData streamData);

  @Post("/pause")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> pause(HttpRequest request, @Valid @Body InputStreamData streamData);

}

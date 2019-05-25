package org.jlom.master_upm.tfm.micronaut.apigw.metrics;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;

@Controller("/custom_metrics")
@Requires(notEnv = Environment.TEST)
@Secured("isAnonymous()")
public class CustomMetricsController {

  private final InitialEventListener listener;

  public CustomMetricsController(InitialEventListener listener) {

    this.listener = listener;
  }

  @Get("/initial-time")
  @Produces(MediaType.APPLICATION_JSON)
  public HttpResponse<?> getInitialTime(HttpRequest request) {
    return HttpResponse.ok(listener.getCurrentTimeMillis());
  }
}

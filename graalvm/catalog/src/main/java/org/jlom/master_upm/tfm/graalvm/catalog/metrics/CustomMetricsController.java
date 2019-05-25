package org.jlom.master_upm.tfm.graalvm.catalog.metrics;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

@Controller("/custom_metrics")
@Requires(notEnv = Environment.TEST)
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

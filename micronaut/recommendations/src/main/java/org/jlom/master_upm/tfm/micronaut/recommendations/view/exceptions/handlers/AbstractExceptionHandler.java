package org.jlom.master_upm.tfm.micronaut.recommendations.view.exceptions.handlers;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import org.jlom.master_upm.tfm.micronaut.recommendations.view.api.dtos.ProblemDetails;


public abstract class AbstractExceptionHandler {
  protected HttpResponse<Object> handleExceptionInternal(Exception ex,
                                                         Object body,
                                                         HttpHeaders headers,
                                                         HttpStatus status,
                                                         HttpRequest request) {
    Object errorResponse;
    if (null == body) {
      errorResponse = new ProblemDetails()
              .status(status.getCode())
              .cause((null != ex.getCause())? ex.getCause().getMessage():"NA")
              .detail(ex.getLocalizedMessage())
              .instance(request.getPath());

    } else {
      errorResponse = body;
    }
    return HttpResponse.status(status)
            .body(errorResponse);
  }
}

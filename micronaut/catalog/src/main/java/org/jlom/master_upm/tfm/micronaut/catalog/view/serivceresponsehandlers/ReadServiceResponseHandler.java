package org.jlom.master_upm.tfm.micronaut.catalog.view.serivceresponsehandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import org.jlom.master_upm.tfm.micronaut.catalog.controller.api.dtos.ContentServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.micronaut.catalog.controller.api.dtos.ContentServiceResponseOk;
import org.jlom.master_upm.tfm.micronaut.catalog.view.api.dtos.ProblemDetails;
import org.jlom.master_upm.tfm.micronaut.catalog.view.exceptions.WrapperException;

import static org.jlom.master_upm.tfm.micronaut.catalog.utils.JsonUtils.ObjectToJson;

public class ReadServiceResponseHandler extends ServiceResponseHandler{

  public ReadServiceResponseHandler(HttpRequest request) {
    super(request);
  }

  @Override
  public HttpResponse<?> handle(ContentServiceResponseOk response) {
    return handleResponseOk(response, HttpStatus.OK);
  }

  @Override
  public HttpResponse<?> handle(ContentServiceResponseFailureNotFound response) {
    final ProblemDetails errorResponse =
            new ProblemDetails()
                    .status(HttpStatus.NO_CONTENT.getCode())
                    .detail(response.getParamName())
                    .instance(getRequest().getPath());

    try {
      return HttpResponse.status(HttpStatus.valueOf(errorResponse.getStatus()))
              .body(ObjectToJson(errorResponse));

    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + errorResponse, e);
    }
  }
}

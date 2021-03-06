package org.jlom.master_upm.tfm.graalvm.catalog.view.serivceresponsehandlers;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos.ContentServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos.ContentServiceResponseOk;
import org.jlom.master_upm.tfm.graalvm.catalog.view.exceptions.InvalidParamException;

public class DeleteServiceResponseHandler extends ServiceResponseHandler {
  public DeleteServiceResponseHandler(HttpRequest request) {
    super(request);
  }

  @Override
  public HttpResponse<?> handle(ContentServiceResponseOk response) {
    return handleResponseOk(response, HttpStatus.OK);
  }

  @Override
  public HttpResponse<?> handle(ContentServiceResponseFailureNotFound response) {
    throw new InvalidParamException(response.getParamName(), response.getMessage());
  }
}

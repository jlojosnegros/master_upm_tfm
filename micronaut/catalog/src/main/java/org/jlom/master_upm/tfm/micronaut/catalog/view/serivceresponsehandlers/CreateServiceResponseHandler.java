package org.jlom.master_upm.tfm.micronaut.catalog.view.serivceresponsehandlers;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import org.jlom.master_upm.tfm.micronaut.catalog.controller.api.dtos.ContentServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.micronaut.catalog.controller.api.dtos.ContentServiceResponseOk;
import org.jlom.master_upm.tfm.micronaut.catalog.view.exceptions.WrapperException;

public class CreateServiceResponseHandler extends ServiceResponseHandler {
  public CreateServiceResponseHandler(HttpRequest<?> request) {
    super(request);
  }

  @Override
  public HttpResponse<?> handle(ContentServiceResponseOk response) {
    return handleResponseOk(response, HttpStatus.CREATED);
  }

  @Override
  public HttpResponse<?> handle(ContentServiceResponseFailureNotFound response) {
    //Not found in a create operation is a internal server error
    throw  new WrapperException("Internal server error:" + response.getMessage());
  }
}

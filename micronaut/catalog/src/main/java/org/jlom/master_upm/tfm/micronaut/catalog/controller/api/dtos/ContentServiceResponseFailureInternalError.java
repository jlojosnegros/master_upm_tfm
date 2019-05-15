package org.jlom.master_upm.tfm.micronaut.catalog.controller.api.dtos;

import io.micronaut.http.HttpResponse;


public class ContentServiceResponseFailureInternalError extends ContentServiceResponseFailure {
  public ContentServiceResponseFailureInternalError(String message) {
    super(message);
  }

  @Override
  public HttpResponse<?> accept(ContentServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

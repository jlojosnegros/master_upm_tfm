package org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.Getter;


@Getter
public class ContentServiceResponseFailureException extends ContentServiceResponseFailure {

  private final Exception exception;

  public ContentServiceResponseFailureException(String message, Exception exception) {
    super(message + " Exception:" + exception.getMessage());
    this.exception = exception;
  }

  public ContentServiceResponseFailureException(Exception exception) {
    this("error: Exception captured. ", exception);
  }

  @Override
  public HttpResponse<?> accept(ContentServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

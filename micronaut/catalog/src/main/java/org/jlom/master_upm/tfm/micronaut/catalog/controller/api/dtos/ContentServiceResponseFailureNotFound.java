package org.jlom.master_upm.tfm.micronaut.catalog.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ContentServiceResponseFailureNotFound extends ContentServiceResponseFailure {

  private final String paramName;
  private final Object value;

  public ContentServiceResponseFailureNotFound(String message, String paramName, Object value) {
    super(message + " element not found for " + paramName + "=" + value.toString());
    this.paramName = paramName;
    this.value = value;
  }

  public ContentServiceResponseFailureNotFound(String paramName, Object value) {
    this("error:", paramName , value);
  }

  @Override
  public HttpResponse<?> accept(ContentServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

package org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

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
  public ResponseEntity<?> accept(ContentServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

package org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos;

import lombok.Getter;
import org.springframework.http.ResponseEntity;

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
  public ResponseEntity<?> accept(ContentServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.StreamControlReturnValue;
import org.springframework.http.ResponseEntity;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class StreamControlServiceResponseFailureException extends StreamControlServiceResponseFailure {

  private final Exception exception;

  public StreamControlServiceResponseFailureException(String message, Exception exception) {
    super(message + " Exception:" + exception.getMessage());
    this.exception = exception;
  }

  public StreamControlServiceResponseFailureException(Exception exception) {
    this("error: Exception captured. ", exception);
  }

  @Override
  public ResponseEntity<?> accept(StreamControlServiceResponseHandler handler) {
    return handler.handle(this);
  }

  @Override
  public StreamControlReturnValue accept(IStreamControlServiceResponseHandlerRPC handler) {
    return handler.handle(this);
  }
}
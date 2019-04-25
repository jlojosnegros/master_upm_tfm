package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos;

import lombok.Getter;
import lombok.ToString;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.StreamControlReturnValue;
import org.springframework.http.ResponseEntity;

@Getter
@ToString
public class StreamControlServiceResponseFailureNotFound extends StreamControlServiceResponseFailure {

  private final String paramName;
  private final Object value;

  public StreamControlServiceResponseFailureNotFound(String message, String paramName, Object value) {
    super(message + " element not found for " + paramName + "=" + value.toString());
    this.paramName = paramName;
    this.value = value;
  }

  public StreamControlServiceResponseFailureNotFound(String paramName, Object value) {
    this("error:", paramName , value);
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
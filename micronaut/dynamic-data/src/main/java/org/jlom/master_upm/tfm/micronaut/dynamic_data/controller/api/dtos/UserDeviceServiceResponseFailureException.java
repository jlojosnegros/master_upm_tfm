package org.jlom.master_upm.tfm.micronaut.dynamic_data.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserDeviceServiceResponseFailureException extends UserDeviceServiceResponseFailure {

  private final Exception exception;

  public UserDeviceServiceResponseFailureException(String message, Exception exception) {
    super(message + " Exception:" + exception.getMessage());
    this.exception = exception;
  }

  public UserDeviceServiceResponseFailureException(Exception exception) {
    this("error: Exception captured. ", exception);
  }

  @Override
  public HttpResponse<?> accept(UserDeviceServiceResponseHandler handler) {
    return handler.handle(this);
  }
}
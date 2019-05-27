package org.jlom.master_upm.tfm.graalvm.dynamic_data.controller.api.dtos;


import io.micronaut.http.HttpResponse;

public class UserDeviceServiceResponseFailureInternalError extends UserDeviceServiceResponseFailure {

  public UserDeviceServiceResponseFailureInternalError(String message) {
    super(message);
  }

  @Override
  public HttpResponse<?> accept(UserDeviceServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

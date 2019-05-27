package org.jlom.master_upm.tfm.graalvm.dynamic_data.controller.api.dtos;


import io.micronaut.http.HttpResponse;

public interface UserDeviceServiceResponseHandler {
  HttpResponse<?> handle(UserDeviceServiceResponseOK response);

  HttpResponse<?> handle(UserDeviceServiceResponseFailureException response);
  HttpResponse<?> handle(UserDeviceServiceResponseFailureInternalError response);
  HttpResponse<?> handle(UserDeviceServiceResponseFailureInvalidInputParameter response);
  HttpResponse<?> handle(UserDeviceServiceResponseFailureNotFound response);
}

package org.jlom.master_upm.tfm.graalvm.dynamic_data.controller.api.dtos;


import io.micronaut.http.HttpResponse;

public interface UserDeviceServiceResponse {
  HttpResponse<?> accept(UserDeviceServiceResponseHandler handler);
}

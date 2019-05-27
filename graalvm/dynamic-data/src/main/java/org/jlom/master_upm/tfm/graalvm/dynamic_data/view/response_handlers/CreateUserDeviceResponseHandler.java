package org.jlom.master_upm.tfm.graalvm.dynamic_data.view.response_handlers;

import  com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import org.jlom.master_upm.tfm.graalvm.dynamic_data.controller.api.dtos.UserDeviceServiceResponseFailureException;
import org.jlom.master_upm.tfm.graalvm.dynamic_data.controller.api.dtos.UserDeviceServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.graalvm.dynamic_data.controller.api.dtos.UserDeviceServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.graalvm.dynamic_data.controller.api.dtos.UserDeviceServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.graalvm.dynamic_data.controller.api.dtos.UserDeviceServiceResponseHandler;
import org.jlom.master_upm.tfm.graalvm.dynamic_data.controller.api.dtos.UserDeviceServiceResponseOK;
import org.jlom.master_upm.tfm.graalvm.dynamic_data.view.exceptions.WrapperException;

import static org.jlom.master_upm.tfm.graalvm.dynamic_data.utils.DtosTransformations.serviceToView;
import static org.jlom.master_upm.tfm.graalvm.dynamic_data.utils.JsonUtils.ObjectToJson;


public class CreateUserDeviceResponseHandler implements UserDeviceServiceResponseHandler {
  private HttpRequest request;

  public CreateUserDeviceResponseHandler(HttpRequest request) {
    this.request = request;
  }

  @Override
  public HttpResponse<?> handle(UserDeviceServiceResponseOK response) {
    try {
      String json = ObjectToJson(serviceToView(response.getUserDevice()));
      return HttpResponse.ok(json);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + response.getUserDevice(), e);
    }

  }

  @Override
  public HttpResponse<?> handle(UserDeviceServiceResponseFailureException response) {
    return null;
  }

  @Override
  public HttpResponse<?> handle(UserDeviceServiceResponseFailureInternalError response) {
    return null;
  }

  @Override
  public HttpResponse<?> handle(UserDeviceServiceResponseFailureInvalidInputParameter response) {
    return null;
  }

  @Override
  public HttpResponse<?> handle(UserDeviceServiceResponseFailureNotFound response) {
    return null;
  }
}

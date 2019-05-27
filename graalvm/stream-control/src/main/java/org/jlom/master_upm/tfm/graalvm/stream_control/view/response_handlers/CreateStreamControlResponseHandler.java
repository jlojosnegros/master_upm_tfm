package org.jlom.master_upm.tfm.graalvm.stream_control.view.response_handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseFailureException;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseHandler;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseOK;
import org.jlom.master_upm.tfm.graalvm.stream_control.view.exceptions.WrapperException;


import static org.jlom.master_upm.tfm.graalvm.stream_control.utils.DtosTransformations.serviceToView;
import static org.jlom.master_upm.tfm.graalvm.stream_control.utils.JsonUtils.ObjectToJson;

public class CreateStreamControlResponseHandler implements StreamControlServiceResponseHandler {
  private HttpRequest request;

  public CreateStreamControlResponseHandler(HttpRequest request) {

    this.request = request;
  }

  @Override
  public HttpResponse<?> handle(StreamControlServiceResponseOK response) {
    try {
      String json = ObjectToJson(serviceToView(response.getStreamControlData()));
      return HttpResponse.ok(json);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + response.getStreamControlData(), e);
    }

  }

  @Override
  public HttpResponse<?> handle(StreamControlServiceResponseFailureException response) {
    return null;
  }

  @Override
  public HttpResponse<?> handle(StreamControlServiceResponseFailureInternalError response) {
    return null;
  }

  @Override
  public HttpResponse<?> handle(StreamControlServiceResponseFailureInvalidInputParameter response) {
    return null;
  }

  @Override
  public HttpResponse<?> handle(StreamControlServiceResponseFailureNotFound response) {
    return null;
  }
}

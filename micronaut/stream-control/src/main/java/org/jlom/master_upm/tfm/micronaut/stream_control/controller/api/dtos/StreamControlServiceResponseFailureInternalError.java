package org.jlom.master_upm.tfm.micronaut.stream_control.controller.api.dtos;


import io.micronaut.http.HttpResponse;
import org.jlom.master_upm.tfm.micronaut.stream_control.view.api.dtos.StreamControlReturnValue;

public class StreamControlServiceResponseFailureInternalError extends StreamControlServiceResponseFailure {

  public StreamControlServiceResponseFailureInternalError(String message) {
    super(message);
  }

  @Override
  public HttpResponse<?> accept(StreamControlServiceResponseHandler handler) {
    return handler.handle(this);
  }

  @Override
  public StreamControlReturnValue accept(IStreamControlServiceResponseHandlerRPC handler) {
    return handler.handle(this);
  }
}

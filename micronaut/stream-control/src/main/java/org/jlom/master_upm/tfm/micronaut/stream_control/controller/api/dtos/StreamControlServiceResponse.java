package org.jlom.master_upm.tfm.micronaut.stream_control.controller.api.dtos;


import io.micronaut.http.HttpResponse;
import org.jlom.master_upm.tfm.micronaut.stream_control.view.api.dtos.StreamControlReturnValue;

public interface StreamControlServiceResponse {
  HttpResponse<?> accept(StreamControlServiceResponseHandler handler);
  StreamControlReturnValue accept(IStreamControlServiceResponseHandlerRPC handler);
}

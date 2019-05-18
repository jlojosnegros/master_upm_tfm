package org.jlom.master_upm.tfm.micronaut.stream_control.controller.api.dtos;


import io.micronaut.http.HttpResponse;

public interface StreamControlServiceResponseHandler {
  HttpResponse<?> handle(StreamControlServiceResponseOK response);

  HttpResponse<?> handle(StreamControlServiceResponseFailureException response);
  HttpResponse<?> handle(StreamControlServiceResponseFailureInternalError response);
  HttpResponse<?> handle(StreamControlServiceResponseFailureInvalidInputParameter response);
  HttpResponse<?> handle(StreamControlServiceResponseFailureNotFound response);
}

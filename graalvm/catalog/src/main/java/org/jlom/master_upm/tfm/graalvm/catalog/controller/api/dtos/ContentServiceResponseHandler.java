package org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos;


import io.micronaut.http.HttpResponse;

public interface ContentServiceResponseHandler {

  HttpResponse<?> handle(ContentServiceResponseOk response);

  HttpResponse<?> handle(ContentServiceResponseFailureException response);
  HttpResponse<?> handle(ContentServiceResponseFailureInternalError response);
  HttpResponse<?> handle(ContentServiceResponseFailureInvalidInputParameter response);
  HttpResponse<?> handle(ContentServiceResponseFailureNotFound response);
}

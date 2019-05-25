package org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos;


import io.micronaut.http.HttpResponse;

public interface ContentServiceResponse {
  HttpResponse<?> accept(ContentServiceResponseHandler handler);
}

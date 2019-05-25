package org.jlom.master_upm.tfm.graalvm.catalog.view.serivceresponsehandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos.ContentServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos.ContentServiceResponseOk;
import org.jlom.master_upm.tfm.graalvm.catalog.view.exceptions.InvalidParamException;
import org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos.ContentServiceResponseFailureException;
import org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos.ContentServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos.ContentServiceResponseHandler;
import org.jlom.master_upm.tfm.graalvm.catalog.view.exceptions.WrapperException;

import static org.jlom.master_upm.tfm.graalvm.catalog.utils.DtosTransformations.serviceToViewContent;
import static org.jlom.master_upm.tfm.graalvm.catalog.utils.JsonUtils.ObjectToJson;


public abstract class ServiceResponseHandler implements ContentServiceResponseHandler {

  private final HttpRequest request;

  public ServiceResponseHandler(HttpRequest request) {
    this.request = request;
  }

  protected HttpRequest getRequest() { return this.request;}

  protected HttpResponse<?> handleResponseOk(ContentServiceResponseOk response, HttpStatus status) {

    try {
      String json = ObjectToJson(serviceToViewContent(response.getContent()));
      return HttpResponse.status(status).body(json);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + response.getContent(), e);
    }
  }

  @Override
  public HttpResponse<?> handle(ContentServiceResponseFailureException response) {
    throw new WrapperException(response.getException());
  }

  @Override
  public HttpResponse<?> handle(ContentServiceResponseFailureInternalError response) {
    throw  new WrapperException("Internal server error:" + response.getMessage());
  }

  @Override
  public HttpResponse<?> handle(ContentServiceResponseFailureInvalidInputParameter response) {
    throw  new InvalidParamException(response.getParamName(), response.getMessage());
  }

}

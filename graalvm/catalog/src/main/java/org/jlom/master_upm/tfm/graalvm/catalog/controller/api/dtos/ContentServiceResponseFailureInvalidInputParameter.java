package org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ContentServiceResponseFailureInvalidInputParameter extends ContentServiceResponseFailure {

  private final String paramName;
  private final Object param;

  public ContentServiceResponseFailureInvalidInputParameter(String message , String paramName, Object param) {
    super (message + ": Parameter " + paramName + " with value " + param.toString());

    this.paramName = paramName;
    this.param = param;
  }
  public ContentServiceResponseFailureInvalidInputParameter(String paramName, Object param) {
    this ("InvalidInputParameter:",paramName,param);
  }

  @Override
  public HttpResponse<?> accept(ContentServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

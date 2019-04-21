package org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

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
  public ResponseEntity<?> accept(ContentServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

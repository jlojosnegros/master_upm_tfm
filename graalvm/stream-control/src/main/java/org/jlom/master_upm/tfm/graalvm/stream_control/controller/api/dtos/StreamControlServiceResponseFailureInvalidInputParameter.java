package org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jlom.master_upm.tfm.graalvm.stream_control.view.api.dtos.StreamControlReturnValue;


@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class StreamControlServiceResponseFailureInvalidInputParameter extends StreamControlServiceResponseFailure {

  private final String paramName;
  private final Object paramValue;

  public StreamControlServiceResponseFailureInvalidInputParameter(String message, String paramName, Object paramValue) {
    super(message + ":Parameter="+paramName+" with value="+paramValue.toString());
    this.paramName = paramName;
    this.paramValue = paramValue;
  }

  public StreamControlServiceResponseFailureInvalidInputParameter(String paramName, Object param) {
    this ("InvalidInputParameter:",paramName,param);
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

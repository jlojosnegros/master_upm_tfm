package org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.Getter;
import lombok.ToString;
import org.jlom.master_upm.tfm.graalvm.stream_control.view.api.dtos.StreamControlReturnValue;

@Getter
@ToString
public class StreamControlServiceResponseFailureNotFound extends StreamControlServiceResponseFailure {

  private final String paramName;
  private final Object value;

  public StreamControlServiceResponseFailureNotFound(String message, String paramName, Object value) {
    super(message + " element not found for " + paramName + "=" + value.toString());
    this.paramName = paramName;
    this.value = value;
  }

  public StreamControlServiceResponseFailureNotFound(String paramName, Object value) {
    this("error:", paramName , value);
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
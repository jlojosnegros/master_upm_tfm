package org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jlom.master_upm.tfm.graalvm.stream_control.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.graalvm.stream_control.view.api.dtos.StreamControlReturnValue;


@Getter
@ToString
@EqualsAndHashCode
public class StreamControlServiceResponseOK implements StreamControlServiceResponse {

  private final StreamControlData streamControlData;

  public StreamControlServiceResponseOK(StreamControlData streamControlData) {
    this.streamControlData = streamControlData;
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

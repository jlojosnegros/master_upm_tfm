package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.StreamControlReturnValue;
import org.springframework.http.ResponseEntity;

@Getter
@ToString
@EqualsAndHashCode
public class StreamControlServiceResponseOK implements StreamControlServiceResponse {

  private final StreamControlData streamControlData;

  public StreamControlServiceResponseOK(StreamControlData streamControlData) {
    this.streamControlData = streamControlData;
  }

  @Override
  public ResponseEntity<?> accept(StreamControlServiceResponseHandler handler) {
    return handler.handle(this);
  }

  @Override
  public StreamControlReturnValue accept(IStreamControlServiceResponseHandlerRPC handler) {
    return handler.handle(this);
  }
}

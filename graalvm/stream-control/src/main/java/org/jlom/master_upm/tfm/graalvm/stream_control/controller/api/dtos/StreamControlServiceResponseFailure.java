package org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public abstract class StreamControlServiceResponseFailure implements StreamControlServiceResponse {

  private final String message;


  public StreamControlServiceResponseFailure(String message) {
    this.message = message;
  }

}

package org.jlom.master_upm.tfm.graalvm.dynamic_data.controller.api.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public abstract class UserDeviceServiceResponseFailure implements UserDeviceServiceResponse {

  private final String message;


  public UserDeviceServiceResponseFailure(String message) {
    this.message = message;
  }

}

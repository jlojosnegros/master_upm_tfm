package org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos.UserDevice;

@Getter
@ToString
@EqualsAndHashCode
public class UserDeviceServiceResponseOK implements UserDeviceServiceResponse {

  private final UserDevice userDevice;

  public UserDeviceServiceResponseOK(UserDevice userDevice) {
    this.userDevice = userDevice;
  }
}

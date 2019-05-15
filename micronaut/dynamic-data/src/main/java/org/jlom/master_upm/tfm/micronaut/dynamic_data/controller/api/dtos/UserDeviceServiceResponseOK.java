package org.jlom.master_upm.tfm.micronaut.dynamic_data.controller.api.dtos;


import io.micronaut.http.HttpResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jlom.master_upm.tfm.micronaut.dynamic_data.model.daos.UserDevice;


@Getter
@ToString
@EqualsAndHashCode
public class UserDeviceServiceResponseOK implements UserDeviceServiceResponse {

  private final UserDevice userDevice;

  public UserDeviceServiceResponseOK(UserDevice userDevice) {
    this.userDevice = userDevice;
  }

  @Override
  public HttpResponse<?> accept(UserDeviceServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

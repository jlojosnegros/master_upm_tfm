package org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

@Getter
@ToString
public class UserDeviceServiceResponseFailureNotFound extends UserDeviceServiceResponseFailure  {

  private final String paramName;
  private final Object value;

  public UserDeviceServiceResponseFailureNotFound (String message, String paramName, Object value) {
    super(message + " element not found for " + paramName + "=" + value.toString());
    this.paramName = paramName;
    this.value = value;
  }

  public UserDeviceServiceResponseFailureNotFound (String paramName, Object value) {
    this("error:", paramName , value);
  }

  @Override
  public ResponseEntity<?> accept(UserDeviceServiceResponseHandler handler) {
    return handler.handle(this);
  }
}
package org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserDeviceServiceResponseFailureInvalidInputParameter extends UserDeviceServiceResponseFailure{

  private final String paramName;
  private final Object paramValue;

  public UserDeviceServiceResponseFailureInvalidInputParameter(String message, String paramName, Object paramValue) {
    super(message + ":Parameter="+paramName+" with value="+paramValue.toString());
    this.paramName = paramName;
    this.paramValue = paramValue;
  }

  public UserDeviceServiceResponseFailureInvalidInputParameter(String paramName, Object param) {
    this ("InvalidInputParameter:",paramName,param);
  }

  @Override
  public ResponseEntity<?> accept(UserDeviceServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

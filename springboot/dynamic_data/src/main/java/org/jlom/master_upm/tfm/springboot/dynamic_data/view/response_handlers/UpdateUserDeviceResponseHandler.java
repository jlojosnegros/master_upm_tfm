package org.jlom.master_upm.tfm.springboot.dynamic_data.view.response_handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseFailureException;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseHandler;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.exceptions.InvalidParamException;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.exceptions.WrapperException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.jlom.master_upm.tfm.springboot.dynamic_data.utils.DtosTransformations.serviceToView;
import static org.jlom.master_upm.tfm.springboot.dynamic_data.utils.JsonUtils.ObjectToJson;

public class UpdateUserDeviceResponseHandler implements UserDeviceServiceResponseHandler {
  private HttpServletRequest request;

  public UpdateUserDeviceResponseHandler(HttpServletRequest request) {
    this.request = request;
  }

  @Override
  public ResponseEntity<?> handle(UserDeviceServiceResponseOK response) {
    try {
      String json = ObjectToJson(serviceToView(response.getUserDevice()));
      return new ResponseEntity<>(json,new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + response.getUserDevice(), e);
    }
  }

  @Override
  public ResponseEntity<?> handle(UserDeviceServiceResponseFailureException response) {
    return null;
  }

  @Override
  public ResponseEntity<?> handle(UserDeviceServiceResponseFailureInternalError response) {
    return null;
  }

  @Override
  public ResponseEntity<?> handle(UserDeviceServiceResponseFailureInvalidInputParameter response) {
    return null;
  }

  @Override
  public ResponseEntity<?> handle(UserDeviceServiceResponseFailureNotFound response) {
    throw new InvalidParamException(response.getParamName(), response.getMessage());
  }
}

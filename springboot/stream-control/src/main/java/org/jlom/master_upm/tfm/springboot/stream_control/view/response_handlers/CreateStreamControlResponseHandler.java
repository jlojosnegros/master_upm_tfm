package org.jlom.master_upm.tfm.springboot.stream_control.view.response_handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseFailureException;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseHandler;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.stream_control.view.exceptions.WrapperException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.jlom.master_upm.tfm.springboot.stream_control.utils.DtosTransformations.serviceToView;
import static org.jlom.master_upm.tfm.springboot.stream_control.utils.JsonUtils.ObjectToJson;

public class CreateStreamControlResponseHandler implements StreamControlServiceResponseHandler {
  private HttpServletRequest request;

  public CreateStreamControlResponseHandler(HttpServletRequest request) {

    this.request = request;
  }

  @Override
  public ResponseEntity<?> handle(StreamControlServiceResponseOK response) {
    try {
      String json = ObjectToJson(serviceToView(response.getStreamControlData()));
      return new ResponseEntity<>(json,new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + response.getStreamControlData(), e);
    }

  }

  @Override
  public ResponseEntity<?> handle(StreamControlServiceResponseFailureException response) {
    return null;
  }

  @Override
  public ResponseEntity<?> handle(StreamControlServiceResponseFailureInternalError response) {
    return null;
  }

  @Override
  public ResponseEntity<?> handle(StreamControlServiceResponseFailureInvalidInputParameter response) {
    return null;
  }

  @Override
  public ResponseEntity<?> handle(StreamControlServiceResponseFailureNotFound response) {
    return null;
  }
}

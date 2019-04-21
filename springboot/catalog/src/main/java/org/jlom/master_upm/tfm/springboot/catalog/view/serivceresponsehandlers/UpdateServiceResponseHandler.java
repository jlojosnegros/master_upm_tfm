package org.jlom.master_upm.tfm.springboot.catalog.view.serivceresponsehandlers;

import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseOk;
import org.jlom.master_upm.tfm.springboot.catalog.view.exceptions.InvalidParamException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public class UpdateServiceResponseHandler extends ServiceResponseHandler {
  public UpdateServiceResponseHandler(HttpServletRequest request) {
    super(request);
  }

  @Override
  public ResponseEntity<?> handle(ContentServiceResponseOk response) {
    return handleResponseOk(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> handle(ContentServiceResponseFailureNotFound response) {
    throw new InvalidParamException(response.getParamName(), response.getMessage());
  }
}

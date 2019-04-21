package org.jlom.master_upm.tfm.springboot.catalog.view.serivceresponsehandlers;

import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseOk;
import org.jlom.master_upm.tfm.springboot.catalog.view.exceptions.WrapperException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public class CreateServiceResponseHandler extends ServiceResponseHandler {
  public CreateServiceResponseHandler(HttpServletRequest request) {
    super(request);
  }

  @Override
  public ResponseEntity<?> handle(ContentServiceResponseOk response) {
    return handleResponseOk(response,HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<?> handle(ContentServiceResponseFailureNotFound response) {
    //Not found in a create operation is a internal server error
    throw  new WrapperException("Internal server error:" + response.getMessage());
  }
}

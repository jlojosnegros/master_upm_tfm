package org.jlom.master_upm.tfm.springboot.catalog.view.serivceresponsehandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseOk;
import org.jlom.master_upm.tfm.springboot.catalog.view.api.dtos.ProblemDetails;
import org.jlom.master_upm.tfm.springboot.catalog.view.exceptions.WrapperException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.jlom.master_upm.tfm.springboot.catalog.utils.JsonUtils.ObjectToJson;

public class ReadServiceResponseHandler extends ServiceResponseHandler{

  public ReadServiceResponseHandler(HttpServletRequest request) {
    super(request);
  }

  @Override
  public ResponseEntity<?> handle(ContentServiceResponseOk response) {
    return handleResponseOk(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> handle(ContentServiceResponseFailureNotFound response) {
    final ProblemDetails errorResponse =
            new ProblemDetails()
                    .status(HttpStatus.NO_CONTENT.value())
                    .detail(response.getParamName())
                    .instance(getRequest().getContextPath());

    try {
      return new ResponseEntity<>(ObjectToJson(errorResponse),
              new HttpHeaders(),
              HttpStatus.valueOf(errorResponse.getStatus()));

    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + errorResponse, e);
    }
  }
}

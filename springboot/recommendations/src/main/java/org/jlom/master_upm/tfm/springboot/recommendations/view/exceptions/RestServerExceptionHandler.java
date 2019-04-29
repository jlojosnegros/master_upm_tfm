package org.jlom.master_upm.tfm.springboot.recommendations.view.exceptions;

import org.jlom.master_upm.tfm.springboot.recommendations.view.api.dtos.ProblemDetails;
 import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestServerExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                           Object body,
                                                           HttpHeaders headers,
                                                           HttpStatus status,
                                                           WebRequest request) {
    Object errorResponse;
    if (null == body) {
      errorResponse = new ProblemDetails()
              .status(status.value())
              .cause((null != ex.getCause())? ex.getCause().getMessage():"NA")
              .detail(ex.getLocalizedMessage())
              .instance(request.getContextPath());

    } else {
      errorResponse = body;
    }

    return super.handleExceptionInternal(ex, errorResponse, headers, status, request);
  }

  @ExceptionHandler({Throwable.class})
  public ResponseEntity<Object> handleAll(Throwable thr, WebRequest request) {
    final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    final Exception exc = thr instanceof Exception ? (Exception) thr : new Exception(thr);
    return handleExceptionInternal(exc, null, new HttpHeaders(), status, request);
  }

  @ExceptionHandler({InvalidParamException.class, IllegalArgumentException.class})
  public ResponseEntity<Object> handleQuery(WrapperException ex, WebRequest request) {
    final HttpStatus status = HttpStatus.BAD_REQUEST;
    final ProblemDetails errorResponse = new ProblemDetails()
            .status(status.value())
            .detail(ex.getMessage())
            .instance(request.getContextPath())
            .cause((null != ex.getCause())? ex.getCause().getMessage():null);


    return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), status, request);
  }

  @ExceptionHandler({WrapperException.class})
  public ResponseEntity<Object> handleNesting(WrapperException ex, WebRequest request) {
    final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    final ProblemDetails errorResponse =
            new ProblemDetails()
                    .status(status.value())
                    .detail(ex.getMessage())
                    .instance(request.getContextPath())
                    .cause((null != ex.getCause())? ex.getCause().getMessage():null);

    return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), status, request);
  }
}

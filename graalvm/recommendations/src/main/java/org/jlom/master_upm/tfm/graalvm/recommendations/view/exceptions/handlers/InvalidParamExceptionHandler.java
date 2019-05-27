package org.jlom.master_upm.tfm.graalvm.recommendations.view.exceptions.handlers;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import org.jlom.master_upm.tfm.graalvm.recommendations.view.exceptions.InvalidParamException;

import javax.inject.Singleton;


@Produces
@Singleton
@Requires(classes = {InvalidParamException.class, ExceptionHandler.class})
public class InvalidParamExceptionHandler extends AbstractExceptionHandler
        implements ExceptionHandler<InvalidParamException, HttpResponse> {

  @Override
  public HttpResponse handle(HttpRequest request, InvalidParamException ex) {
    return handleExceptionInternal(ex,null,null, HttpStatus.BAD_REQUEST,request);
  }
}

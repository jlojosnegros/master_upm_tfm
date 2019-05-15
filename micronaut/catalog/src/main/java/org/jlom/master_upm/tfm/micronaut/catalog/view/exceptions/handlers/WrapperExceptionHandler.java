package org.jlom.master_upm.tfm.micronaut.catalog.view.exceptions.handlers;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import org.jlom.master_upm.tfm.micronaut.catalog.view.exceptions.WrapperException;

import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {WrapperExceptionHandler.class, ExceptionHandler.class})
public class WrapperExceptionHandler extends AbstractExceptionHandler
        implements ExceptionHandler<WrapperException, HttpResponse> {

  @Override
  public HttpResponse handle(HttpRequest request, WrapperException ex) {
    return handleExceptionInternal(ex,null,null, HttpStatus.INTERNAL_SERVER_ERROR,request);
  }
}
package org.jlom.master_upm.tfm.micronaut.stream_control.view.exceptions.handlers;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;


@Produces
@Singleton
@Requires(classes = {Throwable.class, ExceptionHandler.class})
public class ThrowableExceptionHandler extends AbstractExceptionHandler
        implements ExceptionHandler<Throwable, HttpResponse> {

  @Override
  public HttpResponse handle(HttpRequest request, Throwable thr) {
    final Exception ex = thr instanceof Exception ? (Exception) thr : new Exception(thr);
    return handleExceptionInternal(ex,null,null, HttpStatus.INTERNAL_SERVER_ERROR,request);
  }
}
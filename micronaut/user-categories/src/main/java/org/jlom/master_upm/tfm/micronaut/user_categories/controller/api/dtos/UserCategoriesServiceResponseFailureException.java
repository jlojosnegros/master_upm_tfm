package org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserCategoriesServiceResponseFailureException extends UserCategoriesServiceResponseFailure {

  private final Exception exception;

  public UserCategoriesServiceResponseFailureException(String message, Exception exception) {
    super(message + " Exception:" + exception.getMessage());
    this.exception = exception;
  }

  public UserCategoriesServiceResponseFailureException(Exception exception) {
    this("error: Exception captured. ", exception);
  }

  @Override
  public HttpResponse<?> accept(UserCategoriesServiceResponseHandler handler) {
    return handler.handle(this);
  }


}
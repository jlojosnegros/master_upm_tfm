package org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserCategoriesServiceResponseFailureInvalidInputParameter extends UserCategoriesServiceResponseFailure {

  private final String paramName;
  private final Object paramValue;

  public UserCategoriesServiceResponseFailureInvalidInputParameter(String message, String paramName, Object paramValue) {
    super(message + ":Parameter="+paramName+" with value="+paramValue.toString());
    this.paramName = paramName;
    this.paramValue = paramValue;
  }

  public UserCategoriesServiceResponseFailureInvalidInputParameter(String paramName, Object param) {
    this ("InvalidInputParameter:",paramName,param);
  }

  @Override
  public HttpResponse<?> accept(UserCategoriesServiceResponseHandler handler) {
    return handler.handle(this);
  }

}

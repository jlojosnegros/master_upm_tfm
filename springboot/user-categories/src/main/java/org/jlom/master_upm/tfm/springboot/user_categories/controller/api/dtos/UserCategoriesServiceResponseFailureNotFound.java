package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos;

import lombok.Getter;
import lombok.ToString;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.StreamControlReturnValue;
import org.springframework.http.ResponseEntity;

@Getter
@ToString
public class UserCategoriesServiceResponseFailureNotFound extends UserCategoriesServiceResponseFailure {

  private final String paramName;
  private final Object value;

  public UserCategoriesServiceResponseFailureNotFound(String message, String paramName, Object value) {
    super(message + " element not found for " + paramName + "=" + value.toString());
    this.paramName = paramName;
    this.value = value;
  }

  public UserCategoriesServiceResponseFailureNotFound(String paramName, Object value) {
    this("error:", paramName , value);
  }

  @Override
  public ResponseEntity<?> accept(UserCategoriesServiceResponseHandler handler) {
    return handler.handle(this);
  }
}
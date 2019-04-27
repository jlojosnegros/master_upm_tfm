package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos;

import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.StreamControlReturnValue;
import org.springframework.http.ResponseEntity;

public class UserCategoriesServiceResponseFailureInternalError extends UserCategoriesServiceResponseFailure {

  public UserCategoriesServiceResponseFailureInternalError(String message) {
    super(message);
  }

  @Override
  public ResponseEntity<?> accept(UserCategoriesServiceResponseHandler handler) {
    return handler.handle(this);
  }


}

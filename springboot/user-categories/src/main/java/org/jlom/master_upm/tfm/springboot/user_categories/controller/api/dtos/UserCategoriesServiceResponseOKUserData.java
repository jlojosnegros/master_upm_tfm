package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.UserData;
import org.springframework.http.ResponseEntity;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserCategoriesServiceResponseOKUserData extends UserCategoriesServiceResponseOK {
  private final UserData userData;

  public UserCategoriesServiceResponseOKUserData(UserData userData) {
    this.userData = userData;
  }

  @Override
  public ResponseEntity<?> accept(UserCategoriesServiceResponseHandler handler) {
    return handler.handle(this);
  }

}

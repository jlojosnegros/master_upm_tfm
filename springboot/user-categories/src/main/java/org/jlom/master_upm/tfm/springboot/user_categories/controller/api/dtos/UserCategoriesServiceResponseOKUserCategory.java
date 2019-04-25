package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.UserCategory;
import org.springframework.http.ResponseEntity;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserCategoriesServiceResponseOKUserCategory extends UserCategoriesServiceResponseOK {
  private final UserCategory userCategory;

  public UserCategoriesServiceResponseOKUserCategory(UserCategory userCategory) {
    this.userCategory = userCategory;
  }

  @Override
  public ResponseEntity<?> accept(UserCategoriesServiceResponseHandler handler) {
    return handler.handle(this);
  }

}
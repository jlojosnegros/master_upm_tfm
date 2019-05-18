package org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos;


import io.micronaut.http.HttpResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.UserCategory;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserCategoriesServiceResponseOKUserCategory extends UserCategoriesServiceResponseOK {
  private final UserCategory userCategory;

  public UserCategoriesServiceResponseOKUserCategory(UserCategory userCategory) {
    this.userCategory = userCategory;
  }

  @Override
  public HttpResponse<?> accept(UserCategoriesServiceResponseHandler handler) {
    return handler.handle(this);
  }

}
package org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jlom.master_upm.tfm.graalvm.user_categories.model.daos.UserData;


@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserCategoriesServiceResponseOKUserData extends UserCategoriesServiceResponseOK {
  private final UserData userData;

  public UserCategoriesServiceResponseOKUserData(UserData userData) {
    this.userData = userData;
  }

  @Override
  public HttpResponse<?> accept(UserCategoriesServiceResponseHandler handler) {
    return handler.handle(this);
  }

}

package org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public abstract class UserCategoriesServiceResponseFailure implements UserCategoriesServiceResponse {

  private final String message;


  public UserCategoriesServiceResponseFailure(String message) {
    this.message = message;
  }

}

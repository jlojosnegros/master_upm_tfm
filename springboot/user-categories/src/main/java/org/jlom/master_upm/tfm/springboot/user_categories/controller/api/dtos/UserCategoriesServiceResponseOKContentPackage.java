package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.ContentPackage;
import org.springframework.http.ResponseEntity;


@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserCategoriesServiceResponseOKContentPackage extends UserCategoriesServiceResponseOK {
  private final ContentPackage contentPackage;

  public UserCategoriesServiceResponseOKContentPackage(ContentPackage contentPackage) {
    this.contentPackage = contentPackage;
  }

  @Override
  public ResponseEntity<?> accept(UserCategoriesServiceResponseHandler handler) {
    return handler.handle(this);
  }

}

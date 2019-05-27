package org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jlom.master_upm.tfm.graalvm.user_categories.model.daos.ContentPackage;


@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserCategoriesServiceResponseOKContentPackage extends UserCategoriesServiceResponseOK {
  private final ContentPackage contentPackage;

  public UserCategoriesServiceResponseOKContentPackage(ContentPackage contentPackage) {
    this.contentPackage = contentPackage;
  }

  @Override
  public HttpResponse<?> accept(UserCategoriesServiceResponseHandler handler) {
    return handler.handle(this);
  }

}

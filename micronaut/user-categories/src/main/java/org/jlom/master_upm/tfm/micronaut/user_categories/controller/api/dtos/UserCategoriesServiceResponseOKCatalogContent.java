package org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserCategoriesServiceResponseOKCatalogContent extends UserCategoriesServiceResponseOK {
  private Set<CatalogContent> filteredContent;

  public UserCategoriesServiceResponseOKCatalogContent(Set<CatalogContent> filteredContent) {
    this.filteredContent = filteredContent;
  }

  @Override
  public HttpResponse<?> accept(UserCategoriesServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

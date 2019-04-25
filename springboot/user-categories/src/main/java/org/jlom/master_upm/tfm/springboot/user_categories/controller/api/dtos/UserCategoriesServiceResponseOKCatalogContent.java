package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

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
  public ResponseEntity<?> accept(UserCategoriesServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

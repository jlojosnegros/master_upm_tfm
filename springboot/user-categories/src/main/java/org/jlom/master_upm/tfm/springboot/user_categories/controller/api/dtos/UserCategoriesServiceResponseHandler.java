package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos;

import org.springframework.http.ResponseEntity;

public interface UserCategoriesServiceResponseHandler {
  ResponseEntity<?> handle(UserCategoriesServiceResponseOKUserData response);
  ResponseEntity<?> handle(UserCategoriesServiceResponseOKContentPackage response);
  ResponseEntity<?> handle(UserCategoriesServiceResponseOKUserCategory response);
  ResponseEntity<?> handle(UserCategoriesServiceResponseOKCatalogContent response);

  ResponseEntity<?> handle(UserCategoriesServiceResponseFailureException response);
  ResponseEntity<?> handle(UserCategoriesServiceResponseFailureInternalError response);
  ResponseEntity<?> handle(UserCategoriesServiceResponseFailureInvalidInputParameter response);
  ResponseEntity<?> handle(UserCategoriesServiceResponseFailureNotFound response);
}

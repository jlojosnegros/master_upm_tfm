package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos;

import org.springframework.http.ResponseEntity;

public interface UserCategoriesServiceResponse {
  ResponseEntity<?> accept(UserCategoriesServiceResponseHandler handler);
}

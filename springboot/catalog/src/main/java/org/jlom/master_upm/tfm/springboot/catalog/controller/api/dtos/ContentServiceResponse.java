package org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos;

import org.springframework.http.ResponseEntity;

public interface ContentServiceResponse {

  ResponseEntity<?> accept (ContentServiceResponseHandler handler);
}

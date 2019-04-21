package org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos;

import lombok.Getter;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;
import org.springframework.http.ResponseEntity;

public class ContentServiceResponseOk implements ContentServiceResponse {

  @Getter
  private final CatalogContent content;

  public ContentServiceResponseOk(CatalogContent content) {
    this.content = content;
  }

  @Override
  public ResponseEntity<?> accept(ContentServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

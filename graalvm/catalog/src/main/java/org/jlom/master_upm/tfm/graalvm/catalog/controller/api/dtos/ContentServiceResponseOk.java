package org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos;

import io.micronaut.http.HttpResponse;
import lombok.Getter;
import org.jlom.master_upm.tfm.graalvm.catalog.model.CatalogContent;

public class ContentServiceResponseOk implements ContentServiceResponse {

  @Getter
  private final CatalogContent content;

  public ContentServiceResponseOk(CatalogContent content) {
    this.content = content;
  }

  @Override
  public HttpResponse<?> accept(ContentServiceResponseHandler handler) {
    return handler.handle(this);
  }
}

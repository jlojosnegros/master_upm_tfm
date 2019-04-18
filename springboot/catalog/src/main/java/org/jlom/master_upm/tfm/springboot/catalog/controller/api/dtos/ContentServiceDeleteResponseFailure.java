package org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos;

public class ContentServiceDeleteResponseFailure implements ContentServiceDeleteResponse {
  private String message;

  public ContentServiceDeleteResponseFailure(String message) {

    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}

package org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos;

public class ContentServiceCreateResponseFailure implements ContentServiceCreateResponse {
  private final String message;

  public ContentServiceCreateResponseFailure(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}

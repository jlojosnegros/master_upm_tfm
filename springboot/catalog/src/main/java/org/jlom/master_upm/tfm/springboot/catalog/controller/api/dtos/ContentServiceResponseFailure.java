package org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos;

public class ContentServiceResponseFailure implements ContentServiceResponse {
  private final String message;

  public ContentServiceResponseFailure(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}

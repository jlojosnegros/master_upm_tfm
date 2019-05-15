package org.jlom.master_upm.tfm.micronaut.catalog.view.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class InvalidParamException extends WrapperException {

  private String parameter;
  private String message;

  public InvalidParamException(String parameter, String message) {
    super("param: "  + parameter + " => msg:" + message);
    this.parameter = parameter;
    this.message = message;
  }
}

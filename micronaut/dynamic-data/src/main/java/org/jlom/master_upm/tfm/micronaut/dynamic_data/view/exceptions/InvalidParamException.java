package org.jlom.master_upm.tfm.micronaut.dynamic_data.view.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class InvalidParamException extends WrapperException {

  private String parameter;
  private String message;

  public InvalidParamException(String parameter, String message) {
    super("param: "  + parameter + " => msg:" + message);
    this.parameter = parameter;
    this.message = message;
  }
}
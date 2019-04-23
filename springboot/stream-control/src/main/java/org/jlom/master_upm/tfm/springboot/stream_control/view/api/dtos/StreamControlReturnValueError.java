package org.jlom.master_upm.tfm.springboot.stream_control.view.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;


@Builder
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
class StreamControlReturnValueError extends StreamControlReturnValue implements Serializable {
  private final static long serialVersionUID = 1L;

  private final int errorCode;
  private final String message;
}
package org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Builder
public class Device implements Serializable {

  private static final long serialVersionUID = 1L;

  private long deviceId;
  private long userId;

}

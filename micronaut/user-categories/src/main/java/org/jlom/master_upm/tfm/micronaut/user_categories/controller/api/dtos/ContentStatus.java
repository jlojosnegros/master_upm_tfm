package org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos;


import java.io.Serializable;

public enum ContentStatus implements Serializable {
  AVAILABLE,
  UNAVAILABLE,
  SOON,
  ON_HOLD,
  VOTING;

  private static final long serialVersionUID = 1L;
}


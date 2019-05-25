package org.jlom.master_upm.tfm.graalvm.catalog.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(using = ContentStatusJsonSerializer.class)
@JsonDeserialize(using = ContentStatusJsonDeserializer.class)
public enum ContentStatus implements Serializable {
  AVAILABLE,
  UNAVAILABLE,
  SOON,
  ON_HOLD,
  VOTING;

  private static final long serialVersionUID = 1L;
}



package org.jlom.master_upm.tfm.graalvm.stream_control.model.daos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(using = StreamStatusJsonSerializer.class)
@JsonDeserialize(using = StreamStatusJsonDeserializer.class)
public enum StreamStatus implements Serializable {
  RUNNING,
  DONE,
  PAUSED;

  private static final long serialVersionUID = 1L;
}

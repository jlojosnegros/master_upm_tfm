package org.jlom.master_upm.tfm.springboot.stream_control.model.daos;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class StreamControlData implements Serializable {

  private static final long serialVersionUID = 1L;

  private long userId;
  private long deviceId;
  private long streamId;

  private StreamStatus status;
  private boolean tillTheEnd = false;


}

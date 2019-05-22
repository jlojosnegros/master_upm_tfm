package org.jlom.master_upm.tfm.micronaut.stream_control.model.daos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StreamControlData implements Serializable {

  private static final long serialVersionUID = 1L;

  private long userId;
  private long deviceId;
  private long streamId;

  private StreamStatus status;

  @Builder.Default
  private boolean tillTheEnd = false;


}

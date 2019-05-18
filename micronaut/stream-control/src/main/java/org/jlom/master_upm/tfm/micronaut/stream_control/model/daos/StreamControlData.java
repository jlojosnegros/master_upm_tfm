package org.jlom.master_upm.tfm.micronaut.stream_control.model.daos;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Builder
@Data
public class StreamControlData implements Serializable {

  private static final long serialVersionUID = 1L;

  private long userId;
  private long deviceId;
  private long streamId;

  private StreamStatus status;
  @Builder.Default
  private boolean tillTheEnd = false;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StreamControlData that = (StreamControlData) o;
    return userId == that.userId &&
            deviceId == that.deviceId &&
            streamId == that.streamId &&
            tillTheEnd == that.tillTheEnd &&
            status == that.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, deviceId, streamId);
  }
}

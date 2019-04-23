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
  private boolean tillTheEnd;

  public StreamControlData(final long userId, final long deviceId, final long streamId, final StreamStatus status) {
    this.userId = userId;
    this.deviceId = deviceId;
    this.streamId = streamId;
    this.status = status;
    this.tillTheEnd = false;
  }

  public StreamControlData(final long userId, final long deviceId, final long streamId) {
    this(userId,deviceId,streamId,StreamStatus.RUNNING);
  }

  public String getKey() {
    return calculateKeyFor(userId,deviceId,streamId);
  }

  public static String calculateKeyFor(final long userId, final long deviceId, final long streamId) {
    return String.format(":%s:%s:%s:",
            String.valueOf(userId),
            String.valueOf(deviceId),
            String.valueOf(streamId)
    );
  }

}

package org.jlom.master_upm.tfm.springboot.stream_control.controller.api.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StreamControlStreamingNotification implements Serializable {

  private final static long serialVersionUID = 1L;

  public enum Operation {
    PLAY,PAUSE,STOP;
  }

  @JsonProperty(value = "stream-id",required = true)
  private String streamId;

  @JsonProperty(value = "device-id",required = true)
  private String deviceId;

  @JsonProperty(value = "user-id",required = true)
  private String userId;

  @JsonProperty(value = "operation",required = true)
  private Operation operation;

}

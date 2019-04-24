package org.jlom.master_upm.tfm.springboot.stream_control.view.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class StreamControlReturnValueOk extends StreamControlReturnValue implements Serializable {

  private final static long serialVersionUID = 1L;

  @JsonProperty(value = "stream-id",required = true)
  private String streamId;

  @JsonProperty(value = "device-id",required = false)
  private String deviceId;

  @JsonProperty(value = "user-id",required = false)
  private String userId;
}

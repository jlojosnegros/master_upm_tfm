package org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos;

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
public class InputStreamData implements Serializable {

  private final static long serialVersionUID = 1L;

  @JsonProperty(value = "stream-id",required = true)
  private  String streamId;

  @JsonProperty(value = "device-id",required = false)
  private String deviceId;

  @JsonProperty(value = "user-id",required = false)
  private String userId;

}


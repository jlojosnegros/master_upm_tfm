package org.jlom.master_upm.tfm.micronaut.dynamic_data.view.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InputUserDevice implements Serializable {

  private final static long serialVersionUID = 1L;

  @JsonProperty(value = "user-id",required = true)
  private  String userId;

  @JsonProperty(value = "device-ids",required = true)
  private Set<String> devices;
}


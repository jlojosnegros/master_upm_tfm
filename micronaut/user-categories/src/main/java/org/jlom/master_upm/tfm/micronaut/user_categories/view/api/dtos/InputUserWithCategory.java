package org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos;

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
public class InputUserWithCategory implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty(value = "user-id",required = true)
  private String userId;

  @JsonProperty(value = "userCategory",required = true)
  private InputUserCategory userCategory;
}
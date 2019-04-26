package org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InputUserContentFiltered implements Serializable {

  private final static long serialVersionUID = 1L;

  @JsonProperty(value = "user-id",required = true)
  String userId;

  @JsonProperty(value = "contents",required = true)
  List<InputCatalogContent> contents;
}

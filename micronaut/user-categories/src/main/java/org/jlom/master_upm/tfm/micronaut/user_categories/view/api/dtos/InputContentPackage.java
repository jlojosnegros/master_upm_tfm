package org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos;

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
public class InputContentPackage implements Serializable {

  private final static long serialVersionUID = 1L;

  @JsonProperty(value = "package-id",required = true)
  private String packageId;

  @JsonProperty(value = "price",required = true)
  private double price;

  @JsonProperty(value = "name",required = true)
  private String name;

  @JsonProperty(value = "tags-filter",required = true)
  Set<String> tagsFilter;
}

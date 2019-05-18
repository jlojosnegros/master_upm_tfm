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
public class InputUserCategory implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty(value = "category-id",required = true)
  private String categoryId;

  @JsonProperty(value = "name",required = true)
  private String name;

  @JsonProperty(value = "price",required = true)
  private double price;

  @JsonProperty(value = "tag-id",required = true)
  private String tagId;
}

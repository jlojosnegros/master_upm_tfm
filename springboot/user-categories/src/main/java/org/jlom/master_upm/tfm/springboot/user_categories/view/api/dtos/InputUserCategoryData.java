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
public class InputUserCategoryData implements Serializable {

  private final static long serialVersionUID = 1L;

  @JsonProperty(value = "user-id",required = true)
  private String userId;

  @JsonProperty(value = "category-id",required = false)
  private  String categoryId;

  @JsonProperty(value = "package-ids",required = false)
  private List<String> packageIds;

}


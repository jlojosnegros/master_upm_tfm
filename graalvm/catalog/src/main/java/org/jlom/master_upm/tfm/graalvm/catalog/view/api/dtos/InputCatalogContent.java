package org.jlom.master_upm.tfm.graalvm.catalog.view.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Introspected
public class InputCatalogContent implements Serializable {

  private final static long serialVersionUID = 1L;

  @JsonProperty(value = "title",required = true)
  private  String title;

  @JsonProperty(value = "stream-id",required = true)
  private  String streamId;

  @JsonProperty(value = "available",required = true)
  private Date available;

  @JsonProperty(value = "status",required = true)
  private String contentStatus;

  @JsonProperty(value = "tags",required = false)
  private Set<String> tags;

  @JsonProperty(value = "id",required = false)
  private  String contentId;

}





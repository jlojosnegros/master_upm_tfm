package org.jlom.master_upm.tfm.springboot.catalog.view.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Value(staticConstructor = "of")
public class CatalogContent implements Serializable {

  private final static long serialVersionUID = 1L;

  @JsonProperty(value = "title",required = true)
  private final String title;

  @JsonProperty(value = "stream-id",required = true)
  private final String streamId;

  @JsonProperty(value = "available",required = true)
  private final Date available;

  @JsonProperty(value = "tags",required = false)
  private final Set<String> tags;

  @JsonProperty(value = "id",required = false)
  private final String contentId;

}

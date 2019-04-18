package org.jlom.master_upm.tfm.springboot.catalog.view.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;

@Value(staticConstructor = "of")
public class CatalogContent implements Serializable {

  private final static long serialVersionUID = 1L;

  @JsonProperty(value = "title",required = true)
  private final String title;

  @JsonProperty(value = "content-id",required = true)
  private final String contentId;

  @JsonProperty(value = "stream-id",required = true)
  private final String streamId;

  private final Set<String> tags;

}

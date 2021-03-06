package org.jlom.master_upm.tfm.micronaut.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CatalogContent implements Serializable {

  private static final long serialVersionUID = 1L;

  private long contentId;

  @Setter
  private String title;

  @Setter
  private long streamId;

  @Setter
  private Set<String> tags;

  @Setter
  private Date available;

  @Setter
  private ContentStatus status;


}

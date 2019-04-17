package org.jlom.master_upm.tfm.springboot.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data(staticConstructor = "of")
@Builder
public class CatalogContent implements Serializable {

  private static final long serialVersionUID = 1L;


  private long contentId;
  private String title;
  private long streamId;
  private Set<String> tags;
  private ZonedDateTime available;
  private ContentStatus status;
}

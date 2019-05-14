package org.jlom.master_upm.tfm.micronaut.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CatalogContent implements Serializable {

  private static final long serialVersionUID = 1L;

  //@jlom todo @Id
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

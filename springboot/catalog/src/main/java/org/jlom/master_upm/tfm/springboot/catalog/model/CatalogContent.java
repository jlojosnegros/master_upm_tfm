package org.jlom.master_upm.tfm.springboot.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class CatalogContent implements Serializable {

  private static final long serialVersionUID = 1L;


  private long contentId;
  private String title;
  private long streamId;
  private Set<String> tags;
  private DateTime available;
  private ContentStatus status;
}

package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
public class CatalogContent implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
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

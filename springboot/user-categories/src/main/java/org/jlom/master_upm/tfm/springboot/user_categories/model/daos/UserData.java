package org.jlom.master_upm.tfm.springboot.user_categories.model.daos;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Builder
@Data
public class UserData implements Serializable {
  private static final long serialVersionUID = 1L;

  private long userId;
  private String categoryId;
  private Set<String> packageIds;
}

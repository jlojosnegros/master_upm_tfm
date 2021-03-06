package org.jlom.master_upm.tfm.micronaut.user_categories.model.daos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserData implements Serializable {
  private static final long serialVersionUID = 1L;

  private long userId;
  private String categoryId;
  private Set<String> packageIds;
}

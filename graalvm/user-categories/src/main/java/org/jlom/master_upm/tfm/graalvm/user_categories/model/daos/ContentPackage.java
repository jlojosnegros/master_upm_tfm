package org.jlom.master_upm.tfm.graalvm.user_categories.model.daos;

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
public class ContentPackage implements Serializable {

  private static final long serialVersionUID = 1L;

  private String packageId;
  private double price;
  private String name;
  Set<String> tagsFilter;
}

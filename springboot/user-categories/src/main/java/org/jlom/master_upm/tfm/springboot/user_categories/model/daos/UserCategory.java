package org.jlom.master_upm.tfm.springboot.user_categories.model.daos;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Builder
@Data
public class UserCategory implements Serializable {

  private static final long serialVersionUID = 1L;

  private String categoryId;
  private String name;
  private double price;
  private String tagId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserCategory that = (UserCategory) o;
    return Double.compare(that.price, price) == 0 &&
            name.equals(that.name) &&
            tagId.equals(that.tagId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, price, tagId);
  }
}
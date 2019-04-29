package org.jlom.master_upm.tfm.springboot.recommendations.model.daos;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
@Builder
public class WeightedTag implements Serializable {

  private static final long serialVersionUID = 1L;

  private String tagName;
  private long weight;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WeightedTag that = (WeightedTag) o;
    return weight == that.weight &&
            tagName.equals(that.tagName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tagName);
  }
}

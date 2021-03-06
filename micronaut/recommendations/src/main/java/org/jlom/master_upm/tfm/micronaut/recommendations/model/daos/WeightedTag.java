package org.jlom.master_upm.tfm.micronaut.recommendations.model.daos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

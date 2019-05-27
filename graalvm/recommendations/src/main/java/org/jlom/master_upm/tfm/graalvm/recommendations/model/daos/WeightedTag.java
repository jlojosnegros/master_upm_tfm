package org.jlom.master_upm.tfm.graalvm.recommendations.model.daos;

import io.micronaut.core.annotation.Introspected;
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
@Introspected
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

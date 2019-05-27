package org.jlom.master_upm.tfm.graalvm.recommendations.model.api;


import org.jlom.master_upm.tfm.graalvm.recommendations.model.daos.WeightedTag;

import java.util.List;
import java.util.Set;


public interface RecommendationsRepositoryQueries {

  WeightedTag find(String userId, String tagName);
  List<WeightedTag> find(String userId, long top);

  Set<String> listMostViewed(long top);
}

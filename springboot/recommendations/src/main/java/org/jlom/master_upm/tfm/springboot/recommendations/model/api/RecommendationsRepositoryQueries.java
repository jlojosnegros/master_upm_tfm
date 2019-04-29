package org.jlom.master_upm.tfm.springboot.recommendations.model.api;


import org.jlom.master_upm.tfm.springboot.recommendations.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.springboot.recommendations.model.daos.WeightedTag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationsRepositoryQueries {

  WeightedTag find(String userId, String tagName);
  List<WeightedTag> find(String userId, long top);

}

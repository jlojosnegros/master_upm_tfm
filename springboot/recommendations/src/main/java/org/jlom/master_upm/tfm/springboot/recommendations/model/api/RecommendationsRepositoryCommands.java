package org.jlom.master_upm.tfm.springboot.recommendations.model.api;


import org.jlom.master_upm.tfm.springboot.recommendations.model.daos.WeightedTag;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationsRepositoryCommands {
  void save(String userId, WeightedTag tag);
  boolean update(String userId, String tagName, long delta);

  void delete(String userId, String tagName);
  void delete(String userId);
}

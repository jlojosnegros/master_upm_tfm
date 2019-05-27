package org.jlom.master_upm.tfm.micronaut.recommendations.model.api;


import org.jlom.master_upm.tfm.micronaut.recommendations.model.daos.WeightedTag;

import java.io.IOException;

public interface RecommendationsRepositoryCommands {
  void save(String userId, WeightedTag tag) throws IOException;
  boolean update(String userId, String tagName, long delta);

  void delete(String userId, String tagName);
  void delete(String userId);

  void addVisualization(String contentId);
}

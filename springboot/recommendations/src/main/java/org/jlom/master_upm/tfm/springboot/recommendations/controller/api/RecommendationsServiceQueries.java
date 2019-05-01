package org.jlom.master_upm.tfm.springboot.recommendations.controller.api;


import org.jlom.master_upm.tfm.springboot.recommendations.view.api.dtos.InputCatalogContent;

import java.util.List;
import java.util.Set;

public interface RecommendationsServiceQueries {
  List<InputCatalogContent> getTopRecommendationsForUser(final String userId, final long top);
  List<InputCatalogContent> getMostViewed(final long top);
}

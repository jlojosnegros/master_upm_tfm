package org.jlom.master_upm.tfm.micronaut.recommendations.controller.api;


import org.jlom.master_upm.tfm.micronaut.recommendations.view.api.dtos.InputCatalogContent;

import java.util.List;

public interface RecommendationsServiceQueries {
  List<InputCatalogContent> getTopRecommendationsForUser(final String userId, final long top);
  List<InputCatalogContent> getMostViewed(final long top);
}

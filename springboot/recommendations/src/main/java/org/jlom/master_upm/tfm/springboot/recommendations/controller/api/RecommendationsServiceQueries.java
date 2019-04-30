package org.jlom.master_upm.tfm.springboot.recommendations.controller.api;


import org.jlom.master_upm.tfm.springboot.recommendations.view.api.dtos.InputCatalogContent;

import java.util.List;

public interface RecommendationsServiceQueries {
  List<InputCatalogContent> getTopRecommendationsForUser(final String userId, final long top);
}

package org.jlom.master_upm.tfm.springboot.recommendations.model.api;

import org.springframework.stereotype.Repository;

@Repository
public interface IRecommendationsRepository extends RecommendationsRepositoryCommands, RecommendationsRepositoryQueries {
}

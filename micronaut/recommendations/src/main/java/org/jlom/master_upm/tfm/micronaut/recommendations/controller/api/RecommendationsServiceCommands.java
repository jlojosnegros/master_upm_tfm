package org.jlom.master_upm.tfm.micronaut.recommendations.controller.api;


import org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.dtos.InputUserActivity;
import org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.dtos.RecommendationsServiceResponse;

public interface RecommendationsServiceCommands {
  RecommendationsServiceResponse register(final InputUserActivity userActivity);
}

package org.jlom.master_upm.tfm.graalvm.recommendations.controller.api;


import org.jlom.master_upm.tfm.graalvm.recommendations.controller.api.dtos.RecommendationsServiceResponse;
import org.jlom.master_upm.tfm.graalvm.recommendations.controller.api.dtos.InputUserActivity;

public interface RecommendationsServiceCommands {
  RecommendationsServiceResponse register(final InputUserActivity userActivity);
}

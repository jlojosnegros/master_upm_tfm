package org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.dtos;


import io.micronaut.http.HttpResponse;

public interface RecommendationsServiceResponse {
  HttpResponse<?> accept(RecommendationsServiceResponseHandler handler);
}

package org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.dtos;


import io.micronaut.http.HttpResponse;

public interface RecommendationsServiceResponseHandler {
  HttpResponse<?> handle(RecommendationsServiceResponseOK response);

  HttpResponse<?> handle(RecommendationsServiceResponseFailureException response);
  HttpResponse<?> handle(RecommendationsServiceResponseFailureInternalError response);
  HttpResponse<?> handle(RecommendationsServiceResponseFailureInvalidInputParameter response);
  HttpResponse<?> handle(RecommendationsServiceResponseFailureNotFound response);
}

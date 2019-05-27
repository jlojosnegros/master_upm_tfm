package org.jlom.master_upm.tfm.micronaut.recommendations.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.validation.Validated;
import org.jlom.master_upm.tfm.micronaut.recommendations.controller.RecommendationsService;
import org.jlom.master_upm.tfm.micronaut.recommendations.view.api.RecommendationsCommandInterface;
import org.jlom.master_upm.tfm.micronaut.recommendations.view.api.RecommendationsQueryInterface;
import org.jlom.master_upm.tfm.micronaut.recommendations.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.micronaut.recommendations.view.exceptions.WrapperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.jlom.master_upm.tfm.micronaut.recommendations.utils.JsonUtils.ListToJson;



@Controller("/org/jlom/master_upm/tfm/micronaut/recommendations")
@Validated
public class RecommendationsRestServer implements RecommendationsQueryInterface, RecommendationsCommandInterface {

  private static final Logger LOG = LoggerFactory.getLogger(RecommendationsRestServer.class);


  private final RecommendationsService service;

  public RecommendationsRestServer(RecommendationsService service) {
    this.service = service;
  }

  @Override
  public HttpResponse<?> getRecommendationsForUser(HttpRequest request, long userId, long top) {

    List<InputCatalogContent> recommendations = service.getTopRecommendationsForUser(String.valueOf(userId), top);

    try {
      return HttpResponse.ok(ListToJson(recommendations));
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + recommendations, e);
    }
  }

  @Override
  public HttpResponse<?> getMostViewed(HttpRequest request, long top) {
    List<InputCatalogContent> mostViewed = service.getMostViewed(top);
    try {
      return HttpResponse.ok(ListToJson(mostViewed));
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + mostViewed, e);
    }
  }



}

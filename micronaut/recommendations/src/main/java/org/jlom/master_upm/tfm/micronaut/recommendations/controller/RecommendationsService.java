package org.jlom.master_upm.tfm.micronaut.recommendations.controller;


import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.IRecommendationsService;
import org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.dtos.InputUserActivity;
import org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.dtos.RecommendationsServiceResponse;
import org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.dtos.RecommendationsServiceResponseFailureException;
import org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.dtos.RecommendationsServiceResponseOK;
import org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.out.InputUserContentFiltered;
import org.jlom.master_upm.tfm.micronaut.recommendations.model.api.IRecommendationsRepository;
import org.jlom.master_upm.tfm.micronaut.recommendations.model.daos.WeightedTag;
import org.jlom.master_upm.tfm.micronaut.recommendations.view.api.dtos.InputCatalogContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class RecommendationsService implements IRecommendationsService {

  private final IRecommendationsRepository repository;
  private final WeighingScales weighingScales;

  @Inject
  @Client("http://catalog-service:8080")
  private HttpClient httpClient;

  @Inject
  @Client("http://categories-service:8080")
  private HttpClient catalogServiceClient;

  private static final Logger LOG = LoggerFactory.getLogger(RecommendationsService.class);

  public RecommendationsService(IRecommendationsRepository repository,
                                WeighingScales weighingScales) {
    this.repository = repository;
    this.weighingScales = weighingScales;
  }

  @Override
  public RecommendationsServiceResponse register(InputUserActivity userActivity) {


    repository.addVisualization(userActivity.getContentId());

    long weigh = weighingScales.weigh(userActivity.getOperation());

    List<WeightedTag> weightedTags = userActivity.getTags()
            .stream()
            .filter(tag -> !tag.startsWith("cat:"))
            .map(tag -> WeightedTag.builder()
                    .tagName(tag)
                    .weight(weigh)
                    .build()
            )
            .collect(Collectors.toList());

    for (var wtag : weightedTags) {
      if (repository.find(userActivity.getUserId(),wtag.getTagName()) == null) {
        try {
          repository.save(userActivity.getUserId(),wtag);
        } catch (IOException e) {
          LOG.error("InternalError while saving in database:" + e.getMessage());
          return new RecommendationsServiceResponseFailureException(e);
        }
      } else {
        repository.update(userActivity.getUserId(),wtag.getTagName(),wtag.getWeight());
      }
    }
    return new RecommendationsServiceResponseOK();
  }

  @Override
  public List<InputCatalogContent> getTopRecommendationsForUser(String userId, long top) {

    long numberOfTags = repository.find(userId, 10).size();
    List<InputCatalogContent> filteredContents = Collections.emptyList();
    List<InputCatalogContent> catalogContents = null;
    do {
      List<WeightedTag> weightedTags = repository.find(userId, numberOfTags);

      String tagList = weightedTags.stream().
              map(WeightedTag::getTagName).
              collect(Collectors.joining(","));

      catalogContents = getInputCatalogContentsForTags(tagList);
      if (catalogContents == null){
        return null;
      }

      //solo filtramos si al menos tenemos suficientes antes de filtrar ...
      if (catalogContents.size() >= top) {
        //ahora hay que filtrar los contenidos con los que el usuario pueda ver
        // y despues comprobar si tenemos suficientes.
        // si no los tenemos quitamos el ultimo de los tags ( que sera el que menos puntos tendra )
        // y volvemos a intentarlo.
        // asi hasta que tengamos suficientes contenidos o nos quedemos sin tags.
        filteredContents = filterInputCatalogContents(userId,catalogContents);
        if (null == filteredContents) {
          LOG.error("jlom Unable to filter contents");
          return null;
        }
      }
      numberOfTags--;
    }while( (filteredContents.size() < top) && (numberOfTags > 0) );

    if(0 == numberOfTags) {
      filteredContents = filterInputCatalogContents(userId,catalogContents);
      if (null == filteredContents) {
        return null;
      }

    }

    if (top < filteredContents.size()) {
      return filteredContents.subList(0,(int)top);
    }
    return filteredContents;
  }

  @Override
  public List<InputCatalogContent> getMostViewed(long top) {


    Set<String> contentIds = repository.listMostViewed(top);

    return contentIds.stream()
            .map(this::getInputCatalogContentId)
            .filter(Objects::nonNull)
            .limit(top)
            .collect(Collectors.toList());
  }

  private InputCatalogContent getInputCatalogContentId(String contentId) {

    final String catalogServiceSearchUri = "/catalog/content";

    String completeURL = String.format("%s/%s",
            catalogServiceSearchUri,
            contentId
    );

    try {
      InputCatalogContent body = httpClient.toBlocking()
              .retrieve(HttpRequest.GET(completeURL), InputCatalogContent.class);
      LOG.info(" body:" + body);
      return body;
    }catch(HttpClientResponseException ex) {
      LOG.error("Unable to get content from catalog-service: " + completeURL
              + " error: " + ex.getMessage()
      );
      return null;
    }
  }
  private List<InputCatalogContent> getInputCatalogContentsForTags(String tagList) {
    ///@jlom todo esto hay que leerlo de la configuracion
    final String catalogServiceSearchUri = "/catalog/content/exactlyWithTags";

    String completeURL = String.format("%s?tags=%s",
            catalogServiceSearchUri,
            tagList
    );

    try {
      InputCatalogContent[] body = httpClient.toBlocking()
              .retrieve(HttpRequest.GET(completeURL)
                      , InputCatalogContent[].class);
      LOG.info("jlom: body:" + Arrays.toString(body));

      List<InputCatalogContent> catalogContents = Arrays.asList(Objects.requireNonNull(body));
      LOG.info("jlom: content:" + catalogContents);
      return catalogContents;

    } catch (HttpClientResponseException ex) {
      LOG.error("Unable to get content from catalog-service: " + completeURL);
      return null;
    }
  }


  private List<InputCatalogContent> filterInputCatalogContents(String userId
          , List<InputCatalogContent> catalogContents) {

    final String userCategoryFilterURI = "/categories/filter";

    InputUserContentFiltered userCategoryFilterInput = InputUserContentFiltered.builder()
            .userId(userId)
            .contents(catalogContents)
            .build();

    try {
      InputUserContentFiltered output = catalogServiceClient
              .toBlocking()
              .retrieve(HttpRequest.POST(userCategoryFilterURI
                      , userCategoryFilterInput), InputUserContentFiltered.class);
      return output.getContents();

    } catch (HttpClientResponseException ex) {
      LOG.error("Unable to get filtered content from category-service: " + userCategoryFilterURI);
      return null;
    }
  }
}

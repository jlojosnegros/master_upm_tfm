package org.jlom.master_upm.tfm.springboot.recommendations.controller;


import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.IRecommendationsService;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.InputUserActivity;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.RecommendationsServiceResponse;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.RecommendationsServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.out.InputUserContentFiltered;
import org.jlom.master_upm.tfm.springboot.recommendations.model.api.IRecommendationsRepository;
import org.jlom.master_upm.tfm.springboot.recommendations.model.daos.WeightedTag;
import org.jlom.master_upm.tfm.springboot.recommendations.view.api.dtos.InputCatalogContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RecommendationsService implements IRecommendationsService {

  private final IRecommendationsRepository repository;
  private final RestTemplate restTemplate;
  private final WeighingScales weighingScales;

  private static final Logger LOG = LoggerFactory.getLogger(RecommendationsService.class);

  public RecommendationsService(IRecommendationsRepository repository,
                                RestTemplate restTemplate,
                                WeighingScales weighingScales) {
    this.repository = repository;
    this.restTemplate = restTemplate;
    this.weighingScales = weighingScales;
  }

  @Override
  public RecommendationsServiceResponse register(InputUserActivity userActivity) {

    long weigh = weighingScales.weigh(userActivity.getOperation());

    userActivity.getTags()
            .stream()
            .filter( tag -> !tag.startsWith("cat:"))
            .map( tag -> WeightedTag.builder()
                            .tagName(tag)
                            .weight(weigh)
                            .build()
            )
            .forEach(wtag -> repository.save(userActivity.getUserId(),wtag));
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

      catalogContents = getInputCatalogContents(tagList);
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

  private List<InputCatalogContent> getInputCatalogContents(String tagList) {
    ///@jlom todo esto hay que leerlo de la configuracion
    final String catalogServiceUrl = "http://catalog-service";
    final int catalogServicePort = 8080;
    final String catalogServiceSearchUri = "/catalog/content/exactlyWithTags";

    String completeURL = String.format("%s:%d%s?tags=%s",
            catalogServiceUrl,
            catalogServicePort,
            catalogServiceSearchUri,
            tagList
    );


    ResponseEntity<InputCatalogContent[]> responseEntity = restTemplate.getForEntity(completeURL,
            InputCatalogContent[].class);

    InputCatalogContent[] body = responseEntity.getBody();
    if (HttpStatus.OK != responseEntity.getStatusCode()) {
      LOG.error("Unable to get content from catalog-service: " + completeURL);
      return null;
    }
    LOG.info("jlom: body:" + Arrays.toString(responseEntity.getBody()));

    List<InputCatalogContent> catalogContents = Arrays.asList(Objects.requireNonNull(body));
    LOG.info("jlom: content:" + catalogContents);
    return catalogContents;
  }
  private List<InputCatalogContent> filterInputCatalogContents(String userId
          , List<InputCatalogContent> catalogContents) {
    final String userCategoryUrl = "http://categories-service";
    final int userCategoryPort = 8080;
    final String userCategoryFilterURI = "/categories/filter";

    String userCategoryFilterCompleteURL = String.format("%s:%d%s",
            userCategoryUrl,
            userCategoryPort,
            userCategoryFilterURI
    );

    InputUserContentFiltered userCategoryFilterInput = InputUserContentFiltered.builder()
            .userId(userId)
            .contents(catalogContents)
            .build();

    ResponseEntity<InputUserContentFiltered> output = restTemplate.postForEntity(userCategoryFilterCompleteURL,
            userCategoryFilterInput,
            InputUserContentFiltered.class);

    if (HttpStatus.OK != output.getStatusCode()) {
      LOG.error("Unable to get filtered content from category-service: " + userCategoryFilterCompleteURL);
      return null;
    }
    return output.getBody().getContents();
  }
}

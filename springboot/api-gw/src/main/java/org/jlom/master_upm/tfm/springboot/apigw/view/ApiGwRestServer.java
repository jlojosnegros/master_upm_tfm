package org.jlom.master_upm.tfm.springboot.apigw.view;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.springboot.apigw.view.api.ApiGwCommandInterface;
import org.jlom.master_upm.tfm.springboot.apigw.view.api.ApiGwQueryInterface;
import org.jlom.master_upm.tfm.springboot.apigw.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.springboot.apigw.view.exceptions.WrapperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static org.jlom.master_upm.tfm.springboot.apigw.utils.JsonUtils.ListToJson;

@RestController
@RequestMapping("/apigw/content")
@Validated
public class ApiGwRestServer implements ApiGwQueryInterface, ApiGwCommandInterface {

  private static final Logger LOG = LoggerFactory.getLogger(ApiGwRestServer.class);

  private RestTemplate restTemplate;

  public ApiGwRestServer(final RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public ResponseEntity<?> play(HttpServletRequest request, @Valid InputCatalogContent content) {
    return null;
  }

  @Override
  public ResponseEntity<?> listContentAvailableSoon(HttpServletRequest request) {

    //@jlom TODO Read this from configuration
    final String catalogServiceUrl = "http://catalog-service";
    final int catalogServicePort = 8080;
    final String catalogContentSoon = "/catalog/content/status/";
    final String status = "SOON";

    String completeURL = String.format("%s:%d%s/%s",
            catalogServiceUrl,
            catalogServicePort,
            catalogContentSoon,
            status
    );

    ResponseEntity<InputCatalogContent[]> responseEntity = restTemplate.getForEntity(completeURL,
            InputCatalogContent[].class);

    InputCatalogContent[] body = responseEntity.getBody();
    if (HttpStatus.OK != responseEntity.getStatusCode()) {
      LOG.error("Unable to get content from catalog-service: " + completeURL);
      throw new WrapperException("Unable to get content from catalog-service :" + completeURL);
    }
    LOG.info("jlom: body:" + Arrays.toString(responseEntity.getBody()));

    List<InputCatalogContent> catalogContents = Arrays.asList(body);
    LOG.info("jlom: content:" + catalogContents);

    try {
      return new ResponseEntity<>(ListToJson(catalogContents), new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + catalogContents, e);
    }
  }

  @Override
  public ResponseEntity<?> listMostViewedContent(HttpServletRequest request, long top) {

    //@jlom TODO Read this from configuration
    final String recommendationsServiceURL = "http://recommendations-service";
    final int recommendationsServicePort = 8080;
    final String recommendationsMosViewedURI = "/recommendations/most-viewed";

    String completeURL = String.format("%s:%d%s/%d",
            recommendationsServiceURL,
            recommendationsServicePort,
            recommendationsMosViewedURI,
            top
    );

    ResponseEntity<InputCatalogContent[]> responseEntity = restTemplate.getForEntity(completeURL,
            InputCatalogContent[].class);

    InputCatalogContent[] body = responseEntity.getBody();
    if (HttpStatus.OK != responseEntity.getStatusCode()) {
      LOG.error("Unable to get content from recommended-service: " + completeURL);
      throw new WrapperException("Unable to get content from recommended-service :" + completeURL);
    }
    LOG.info("jlom: body:" + Arrays.toString(responseEntity.getBody()));

    List<InputCatalogContent> catalogContents = Arrays.asList(body);
    LOG.info("jlom: content:" + catalogContents);

    try {
      return new ResponseEntity<>(ListToJson(catalogContents), new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + catalogContents, e);
    }
  }

  @Override
  public ResponseEntity<?> listMostViewedContent(HttpServletRequest request) {
    return this.listMostViewedContent(request,10);
  }

  @Override
  public ResponseEntity<?> listRecommended(HttpServletRequest request, long userId, long top) {

    //@jlom TODO Read this from configuration
    final String recommendationsServiceURL = "http://recommendations-service";
    final int recommendationsServicePort = 8080;
    final String recommendedForUserURI = "/recommendations/users";

    String completeURL = String.format("%s:%d%s/%d/%d",
            recommendationsServiceURL,
            recommendationsServicePort,
            recommendedForUserURI,
            userId,
            top
    );

    ResponseEntity<InputCatalogContent[]> responseEntity = restTemplate.getForEntity(completeURL,
            InputCatalogContent[].class);

    InputCatalogContent[] body = responseEntity.getBody();
    if (HttpStatus.OK != responseEntity.getStatusCode()) {
      LOG.error("Unable to get content from recommendations-service: " + completeURL);
      throw new WrapperException("Unable to get content from recommendations-service :" + completeURL);
    }
    LOG.info("jlom: body:" + Arrays.toString(responseEntity.getBody()));

    List<InputCatalogContent> catalogContents = Arrays.asList(body);
    LOG.info("jlom: content:" + catalogContents);

    try {
      return new ResponseEntity<>(ListToJson(catalogContents), new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + catalogContents, e);
    }
  }

  @Override
  public ResponseEntity<?> listLiked(HttpServletRequest request, long userId, long top) {
    return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }
}

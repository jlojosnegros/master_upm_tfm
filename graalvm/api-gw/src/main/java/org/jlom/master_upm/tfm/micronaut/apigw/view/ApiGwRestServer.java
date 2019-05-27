package org.jlom.master_upm.tfm.micronaut.apigw.view;


import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.validation.Validated;
import org.jlom.master_upm.tfm.micronaut.apigw.view.api.ApiGwCommandInterface;
import org.jlom.master_upm.tfm.micronaut.apigw.view.api.ApiGwQueryInterface;
import org.jlom.master_upm.tfm.micronaut.apigw.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.micronaut.apigw.view.exceptions.WrapperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static org.jlom.master_upm.tfm.micronaut.apigw.utils.JsonUtils.ListToJson;


@Controller("/apigw/content")
@Validated
public class ApiGwRestServer implements ApiGwQueryInterface, ApiGwCommandInterface {

  private static final Logger LOG = LoggerFactory.getLogger(ApiGwRestServer.class);


  @Inject
  @Client("http://catalog-service:8080")
  HttpClient catalogHttpClient;

  @Inject
  @Client("http://recommendations-service:8080")
  HttpClient recommendationsHttpClient;


  @Override
  public HttpResponse<?> play(HttpRequest request, @Valid InputCatalogContent content) {
    return null;
  }

  @Override
  public HttpResponse<?> listContentAvailableSoon(HttpRequest request) {

    final String catalogContentSoon = "/catalog/content/status/";
    final String status = "SOON";

    String completeURL = String.format("%s/%s",
            catalogContentSoon,
            status
    );

    try {
      InputCatalogContent[] body = catalogHttpClient.toBlocking()
              .retrieve(HttpRequest.GET(completeURL), InputCatalogContent[].class);

      LOG.info("jlom: body:" + Arrays.toString(body));

      List<InputCatalogContent> catalogContents = Arrays.asList(body);
      LOG.info("jlom: content:" + catalogContents);

      return HttpResponse.ok(ListToJson(catalogContents));

    } catch (HttpClientResponseException ex) {
      LOG.error("Unable to get content from catalog-service: " + completeURL);
      throw new WrapperException("Unable to get content from catalog-service :" + completeURL);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + e);
    }
  }

  @Override
  public HttpResponse<?> listMostViewedContent(HttpRequest request, long top) {

    final String recommendationsMosViewedURI = "/recommendations/most-viewed";

    String completeURL = String.format("%s/%d",
            recommendationsMosViewedURI,
            top
    );

    try {
      InputCatalogContent[] body = recommendationsHttpClient
              .toBlocking()
              .retrieve(HttpRequest.GET(completeURL), InputCatalogContent[].class);

      LOG.info("jlom: body:" + Arrays.toString(body));

      List<InputCatalogContent> catalogContents = Arrays.asList(body);
      LOG.info("jlom: content:" + catalogContents);

      return HttpResponse.ok(ListToJson(catalogContents));

    }catch (HttpClientResponseException ex) {
      LOG.error("Unable to get content from recommended-service: " + completeURL);
      throw new WrapperException("Unable to get content from recommended-service :" + completeURL);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new WrapperException("error: Unable to convertToJson obj: " + e);
    }
  }

  @Override
  public HttpResponse<?> listMostViewedContent(HttpRequest request) {
    return this.listMostViewedContent(request,10);
  }

  @Override
  public HttpResponse<?> listRecommended(HttpRequest request, long userId, long top) {

    final String recommendedForUserURI = "/recommendations/users";

    String completeURL = String.format("%s/%d/%d",
            recommendedForUserURI,
            userId,
            top
    );

    try {
      InputCatalogContent[] body = recommendationsHttpClient
              .toBlocking()
              .retrieve(HttpRequest.GET(completeURL), InputCatalogContent[].class);

      LOG.info("jlom: body:" + Arrays.toString(body));
      List<InputCatalogContent> catalogContents = Arrays.asList(body);
      LOG.info("jlom: content:" + catalogContents);

      return HttpResponse.ok(ListToJson(catalogContents));

    } catch (HttpClientResponseException ex) {
      LOG.error("Unable to get content from recommendations-service: " + completeURL);
      throw new WrapperException("Unable to get content from recommendations-service :" + completeURL);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new WrapperException("error: Unable to convertToJson obj: " +  e);
    }
  }

  @Override
  public HttpResponse<?> listLiked(HttpRequest request, long userId, long top) {
    return HttpResponse.badRequest();
  }
}

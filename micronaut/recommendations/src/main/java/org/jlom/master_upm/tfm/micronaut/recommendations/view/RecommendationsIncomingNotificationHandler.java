package org.jlom.master_upm.tfm.micronaut.recommendations.view;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.jlom.master_upm.tfm.micronaut.recommendations.controller.RecommendationsService;
import org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.dtos.InputUserActivity;
import org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.dtos.RecommendationsServiceResponse;
import org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.dtos.RecommendationsServiceResponseOK;
import org.jlom.master_upm.tfm.micronaut.recommendations.controller.api.dtos.UserActivityOperation;
import org.jlom.master_upm.tfm.micronaut.recommendations.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.micronaut.recommendations.view.api.dtos.StreamControlStreamingNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecommendationsIncomingNotificationHandler {

  private static final Logger LOG = LoggerFactory.getLogger(RecommendationsIncomingNotificationHandler.class);


  private final RecommendationsService service;

  @Inject
  @Client("http://catalog-service:8080")
  private HttpClient catalogHttpClient;


  public RecommendationsIncomingNotificationHandler(RecommendationsService service) {

    this.service = service;

  }

  public void incomingStreamControlNotifications(StreamControlStreamingNotification notification) {

    LOG.debug("Incoming notification: " + notification);
    if (notification.getOperation() != StreamControlStreamingNotification.Operation.PLAY) {
      LOG.info("Not handling this operations: " + notification);
      return;
    }

    String streamId = notification.getStreamId();

    InputCatalogContent inputCatalogContent = getInputCatalogContent(streamId);
    if (null == inputCatalogContent) {
      LOG.error("Unable to get content from incoming notification: " + notification);
      return;
    }

    InputUserActivity userActivity = InputUserActivity.builder()
            .contentId(inputCatalogContent.getContentId())
            .operation(UserActivityOperation.WATCH)
            .userId(notification.getUserId())
            .tags(inputCatalogContent.getTags())
            .build();

    RecommendationsServiceResponse response = service.register(userActivity);
    if(!(response instanceof RecommendationsServiceResponseOK)) {
      LOG.error("Error while registering activity:" + userActivity);
    }
  }


  private InputCatalogContent getInputCatalogContent(String streamId) {

    final String catalogServiceSearchUri = "/catalog/content/stream";

    String completeURL = String.format("%s/%s",
            catalogServiceSearchUri,
            streamId
    );

    try {
      InputCatalogContent body = catalogHttpClient
              .toBlocking()
              .retrieve(HttpRequest.GET(completeURL), InputCatalogContent.class);
      LOG.info("jlom: body:" + body);
      return body;
    }catch (HttpClientResponseException ex) {
      LOG.error("Unable to get content from catalog-service: " + completeURL
              + " error:" + ex.getMessage()
      );
      return null;
    }
  }
}

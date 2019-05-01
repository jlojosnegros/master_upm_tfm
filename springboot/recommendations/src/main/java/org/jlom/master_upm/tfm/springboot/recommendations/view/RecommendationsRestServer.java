package org.jlom.master_upm.tfm.springboot.recommendations.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.RecommendationsService;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.InputUserActivity;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.RecommendationsServiceResponse;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.RecommendationsServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.UserActivityOperation;
import org.jlom.master_upm.tfm.springboot.recommendations.view.api.InBoundNotifications;
import org.jlom.master_upm.tfm.springboot.recommendations.view.api.RecommendationsCommandInterface;
import org.jlom.master_upm.tfm.springboot.recommendations.view.api.RecommendationsQueryInterface;
import org.jlom.master_upm.tfm.springboot.recommendations.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.springboot.recommendations.view.api.dtos.StreamControlStreamingNotification;
import org.jlom.master_upm.tfm.springboot.recommendations.view.exceptions.WrapperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.jlom.master_upm.tfm.springboot.recommendations.utils.JsonUtils.ListToJson;


@RestController
@RequestMapping("/recommendations")
@Validated
public class RecommendationsRestServer implements RecommendationsQueryInterface, RecommendationsCommandInterface {

  private static final Logger LOG = LoggerFactory.getLogger(RecommendationsRestServer.class);


  private final RecommendationsService service;
  private final InBoundNotifications incomingNotifications;
  private final RestTemplate restTemplate;

  public RecommendationsRestServer(RecommendationsService service,
                                   InBoundNotifications incomingNotifications,
                                   RestTemplate restTemplate) {
    this.service = service;
    this.incomingNotifications = incomingNotifications;
    this.restTemplate = restTemplate;
  }

  @Override
  public ResponseEntity<?> getRecommendationsForUser(HttpServletRequest request, long userId, long top) {

    List<InputCatalogContent> recommendations = service.getTopRecommendationsForUser(String.valueOf(userId), top);

    try {
      return new ResponseEntity<>(ListToJson(recommendations), new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + recommendations, e);
    }
  }

  @StreamListener(target = InBoundNotifications.StreamControlNotifications)
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

    ///@jlom todo esto hay que leerlo de la configuracion
    final String catalogServiceUrl = "http://catalog-service";
    final int catalogServicePort = 8080;
    final String catalogServiceSearchUri = "/catalog/content/stream";

    String completeURL = String.format("%s:%d%s/%s",
            catalogServiceUrl,
            catalogServicePort,
            catalogServiceSearchUri,
            streamId
    );


    ResponseEntity<InputCatalogContent> responseEntity = restTemplate.getForEntity(completeURL,
            InputCatalogContent.class);
    if (HttpStatus.OK != responseEntity.getStatusCode()) {
      LOG.error("Unable to get content from catalog-service: " + completeURL);
      return null;
    }

    InputCatalogContent body = responseEntity.getBody();
    LOG.info("jlom: body:" + body);

    return body;
  }
}

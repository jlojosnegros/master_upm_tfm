package org.jlom.master_upm.tfm.micronaut.recommendations.view;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.micronaut.recommendations.model.api.IRecommendationsRepository;
import org.jlom.master_upm.tfm.micronaut.recommendations.model.daos.WeightedTag;
import org.jlom.master_upm.tfm.micronaut.recommendations.rabbitmq.TestSender;
import org.jlom.master_upm.tfm.micronaut.recommendations.utils.EmbeddedRedisServer;
import org.jlom.master_upm.tfm.micronaut.recommendations.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.micronaut.recommendations.view.api.dtos.StreamControlStreamingNotification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.jlom.master_upm.tfm.micronaut.recommendations.utils.JsonUtils.ObjectToJson;

@MicronautTest
public class ViewTest {

  private static final Logger LOG = LoggerFactory.getLogger(ViewTest.class);

  @Inject
  private IRecommendationsRepository repository;

  @Inject
  private TestSender notificationSender;

  @Inject
  @Client("/")
  private HttpClient client;

  static WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(8080));

  @Inject
  EmbeddedRedisServer embeddedRedisServer;


  @BeforeAll
  public static void init() {
    wireMockServer.start();
  }

  @AfterAll
  public  static void shutdown() {
    wireMockServer.stop();
  }

  @BeforeEach
  public void setup() {
    embeddedRedisServer.start();
    wireMockServer.resetAll();
  }

  @AfterEach
  public void tearDown() {
    embeddedRedisServer.stop();
  }

  @Test
  public void register() throws JsonProcessingException, InterruptedException {

    final String streamId = "12345";
    final String userId ="666";

    final String catalogServiceSearchUri = "/catalog/content/stream";

    InputCatalogContent content = InputCatalogContent.builder()
            .title("one")
            .tags(Set.of("tag_1", "cat:gold", "tag_2"))
            .streamId(streamId)
            .contentStatus("AVAILABLE")
            .contentId("1111")
            .available(Date.from(Instant.now()))
            .build();

    String completeURL = String.format("%s/%s",
            catalogServiceSearchUri,
            streamId
    );
    stubFor(get(urlEqualTo(completeURL))
            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.getCode())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                    .withBody(ObjectToJson(content))));


    StreamControlStreamingNotification notification =
            StreamControlStreamingNotification.builder()
            .deviceId("1")
            .operation(StreamControlStreamingNotification.Operation.PLAY)
            .streamId(streamId)
            .userId(userId)
            .build();

    notificationSender.sendNotification(notification);
    Thread.sleep(100);

    List<WeightedTag> expected = List.of(WeightedTag.builder()
                    .weight(10)
                    .tagName("tag_1")
                    .build(),
            WeightedTag.builder()
                    .weight(10)
                    .tagName("tag_2")
                    .build()
    );

    List<WeightedTag> weightedTags = repository.find(userId, 5);
    Assertions.assertThat(weightedTags)
            .containsExactlyInAnyOrder(expected.toArray(new WeightedTag[0]));

  }

  @Test
  public void mostViewed() throws IOException, InterruptedException {

    final String streamId = "12345";
    final String streamId_2 = "6789";
    final String userId ="666";
    final String contentId = "1111";
    final String contentId_2 = "2222";

    InputCatalogContent content = InputCatalogContent.builder()
            .title("one")
            .tags(Set.of("tag_1", "cat:gold", "tag_2"))
            .streamId(streamId)
            .contentStatus("AVAILABLE")
            .contentId(contentId)
            .available(Date.from(Instant.now()))
            .build();

    InputCatalogContent content_2= InputCatalogContent.builder()
            .title("two")
            .tags(Set.of("tag_3", "cat:silver", "tag_2"))
            .streamId(streamId_2)
            .contentStatus("AVAILABLE")
            .contentId(contentId_2)
            .available(Date.from(Instant.now()))
            .build();

    final String baseURLstreamId = "/catalog/content/stream";
    String urlStreamId_1 = String.format("%s/%s",
            baseURLstreamId,
            streamId
    );
    stubFor(get(urlEqualTo(urlStreamId_1))
            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.getCode())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                    .withBody(ObjectToJson(content))));

    String urlStreamId_2 = String.format("%s/%s",
            baseURLstreamId,
            streamId_2
    );
    stubFor(get(urlEqualTo(urlStreamId_2))
            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.getCode())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                    .withBody(ObjectToJson(content_2))));

    final String contentBaseUrl = "/catalog/content";
    String contentOneUrl = String.format("%s/%s",
            contentBaseUrl,
            contentId
    );
    stubFor(get(urlEqualTo(contentOneUrl))
            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.getCode())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                    .withBody(ObjectToJson(content))));

    String contentTwoUrl = String.format("%s/%s",
            contentBaseUrl,
            contentId_2
    );
    stubFor(get(urlEqualTo(contentTwoUrl))
            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.getCode())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                    .withBody(ObjectToJson(content_2))));


    StreamControlStreamingNotification notification =
            StreamControlStreamingNotification.builder()
                    .deviceId("1")
                    .operation(StreamControlStreamingNotification.Operation.PLAY)
                    .streamId(streamId)
                    .userId(userId)
                    .build();

    notificationSender.sendNotification(notification);
    Thread.sleep(100);

    notificationSender.sendNotification(notification);
    Thread.sleep(100);

    StreamControlStreamingNotification notification_2 =
            StreamControlStreamingNotification.builder()
                    .deviceId("2")
                    .operation(StreamControlStreamingNotification.Operation.PLAY)
                    .streamId(streamId_2)
                    .userId(userId)
                    .build();

    notificationSender.sendNotification(notification_2);
    Thread.sleep(100);

    List<WeightedTag> expected = List.of(WeightedTag.builder()
                    .weight(20)
                    .tagName("tag_1")
                    .build(),
            WeightedTag.builder()
                    .weight(30)
                    .tagName("tag_2")
                    .build(),
            WeightedTag.builder()
                    .weight(10)
                    .tagName("tag_3")
                    .build()
    );

    List<WeightedTag> weightedTags = repository.find(userId, 5);
    Assertions.assertThat(weightedTags)
            .containsExactlyInAnyOrder(expected.toArray(new WeightedTag[0]));


    //when
    try {
      InputCatalogContent[] catalogContents = client
              .toBlocking()
              .retrieve(HttpRequest.GET("/recommendations/most-viewed/10")
                      , InputCatalogContent[].class);

      LOG.info("received: " + Arrays.toString(catalogContents));

      Assertions.assertThat(catalogContents).containsExactly(content,content_2);

    } catch (HttpClientResponseException ex) {
      Assertions.fail("exception:" + ex.getMessage());
    }
  }
}

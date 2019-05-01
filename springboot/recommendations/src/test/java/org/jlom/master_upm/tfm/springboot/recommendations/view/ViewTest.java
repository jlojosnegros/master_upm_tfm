package org.jlom.master_upm.tfm.springboot.recommendations.view;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.springboot.recommendations.model.api.IRecommendationsRepository;
import org.jlom.master_upm.tfm.springboot.recommendations.model.daos.WeightedTag;
import org.jlom.master_upm.tfm.springboot.recommendations.utils.JsonUtils;
import org.jlom.master_upm.tfm.springboot.recommendations.view.api.InBoundNotifications;
import org.jlom.master_upm.tfm.springboot.recommendations.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.springboot.recommendations.view.api.dtos.StreamControlStreamingNotification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.jlom.master_upm.tfm.springboot.recommendations.utils.JsonUtils.ObjectToJson;
import static org.jlom.master_upm.tfm.springboot.recommendations.utils.JsonUtils.retrieveListOfResourcesFromResponse;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8080)
@ActiveProfiles("test")
public class ViewTest {


  @LocalServerPort
  private int port;

  @Autowired
  private IRecommendationsRepository repository;

  @Autowired
  private InBoundNotifications streamingNotifications;

  @Autowired
  private MessageCollector messageCollector;

  private static final Logger LOG = LoggerFactory.getLogger(ViewTest.class);

  private static int redisEmbeddedServerPort = 6379;
  private RedisServer redisEmbeddedServer = new RedisServer(redisEmbeddedServerPort);

  @Before
  public void setup() {
    redisEmbeddedServer.start();
    String redisContainerIpAddress = "localhost";
    int redisFirstMappedPort = redisEmbeddedServerPort;
    LOG.info("-=* Redis Container running on: " + redisContainerIpAddress + ":" + redisFirstMappedPort);
  }

  @After
  public void tearDown() {
    redisEmbeddedServer.stop();
  }


  private HttpResponse getRestResponseTo(String resourceUri) throws IOException {
    HttpClient client = HttpClientBuilder.create().build();
    return client.execute(new HttpGet("http://localhost:"+port+resourceUri));
  }



  private HttpPost createPostRequest(String resourceUri) {
    HttpPost postRequest = new HttpPost("http://localhost:"+port+resourceUri);
    postRequest.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return postRequest;
  }

  private HttpResponse postMessageTo(String resourceUri) throws IOException {

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPost postRequest = createPostRequest(resourceUri);
    return httpClient.execute(postRequest);
  }



  private HttpResponse postMessageTo(String resourceUri, Object body) throws IOException {

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPost postRequest = createPostRequest(resourceUri);

    String jsonBody = ObjectToJson(body);
    StringEntity entityBody = new StringEntity(jsonBody);
    postRequest.setEntity(entityBody);

    return httpClient.execute(postRequest);
  }

  @Test
  public void register() throws JsonProcessingException {

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
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(ObjectToJson(content))));


    StreamControlStreamingNotification notification =
            StreamControlStreamingNotification.builder()
            .deviceId("1")
            .operation(StreamControlStreamingNotification.Operation.PLAY)
            .streamId(streamId)
            .userId(userId)
            .build();

    streamingNotifications.streamingNotifications()
            .send(MessageBuilder.withPayload(notification)
                    .build()
            );


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
  public void mostViewed() throws IOException {

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
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(ObjectToJson(content))));

    String urlStreamId_2 = String.format("%s/%s",
            baseURLstreamId,
            streamId_2
    );
    stubFor(get(urlEqualTo(urlStreamId_2))
            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(ObjectToJson(content_2))));

    final String contentBaseUrl = "/catalog/content";
    String contentOneUrl = String.format("%s/%s",
            contentBaseUrl,
            contentId
    );
    stubFor(get(urlEqualTo(contentOneUrl))
            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(ObjectToJson(content))));

    String contentTwoUrl = String.format("%s/%s",
            contentBaseUrl,
            contentId_2
    );
    stubFor(get(urlEqualTo(contentTwoUrl))
            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(ObjectToJson(content_2))));


    StreamControlStreamingNotification notification =
            StreamControlStreamingNotification.builder()
                    .deviceId("1")
                    .operation(StreamControlStreamingNotification.Operation.PLAY)
                    .streamId(streamId)
                    .userId(userId)
                    .build();

    streamingNotifications.streamingNotifications()
            .send(MessageBuilder.withPayload(notification)
                    .build()
            );
    streamingNotifications.streamingNotifications()
            .send(MessageBuilder.withPayload(notification)
                    .build()
            );

    StreamControlStreamingNotification notification_2 =
            StreamControlStreamingNotification.builder()
                    .deviceId("2")
                    .operation(StreamControlStreamingNotification.Operation.PLAY)
                    .streamId(streamId_2)
                    .userId(userId)
                    .build();

    streamingNotifications.streamingNotifications()
            .send(MessageBuilder.withPayload(notification_2)
                    .build()
            );


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
    HttpResponse response = getRestResponseTo("/recommendations/most-viewed/10");


    //then
    int statusCode = response.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(200);

    List<InputCatalogContent> catalogContents = retrieveListOfResourcesFromResponse(response, InputCatalogContent.class);

    LOG.info("received: " + catalogContents);

    Assertions.assertThat(catalogContents).containsExactly(content,content_2);
  }

}

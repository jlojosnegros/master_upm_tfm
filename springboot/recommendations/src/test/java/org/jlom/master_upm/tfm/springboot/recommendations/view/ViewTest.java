package org.jlom.master_upm.tfm.springboot.recommendations.view;


import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;



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
                    .withBody(JsonUtils.ObjectToJson(content))));


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

}

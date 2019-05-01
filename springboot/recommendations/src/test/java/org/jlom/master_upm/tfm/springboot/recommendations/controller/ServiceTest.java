package org.jlom.master_upm.tfm.springboot.recommendations.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.InputUserActivity;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.RecommendationsServiceResponse;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.RecommendationsServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.UserActivityOperation;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.in.InBoundNotifications;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.out.InputUserContentFiltered;
import org.jlom.master_upm.tfm.springboot.recommendations.model.api.IRecommendationsRepository;
import org.jlom.master_upm.tfm.springboot.recommendations.model.daos.WeightedTag;
import org.jlom.master_upm.tfm.springboot.recommendations.utils.JsonUtils;
import org.jlom.master_upm.tfm.springboot.recommendations.view.api.dtos.InputCatalogContent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8080)
@ActiveProfiles("test")
public class ServiceTest {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceTest.class);

  @Autowired
  private RecommendationsService service;

  @Autowired
  private IRecommendationsRepository repository;

  private static int redisEmbeddedServerPort = 6379;
  private RedisServer redisEmbeddedServer = new RedisServer(redisEmbeddedServerPort);

//  @Rule
//  public WireMockRule wireMockRule = new WireMockRule(8080); // No-args constructor defaults to port 8080

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
  public void getRecommendationsForUse() throws JsonProcessingException {

    String userId = "1234";
    String[] tags = {"tag_1", "tag_2", "tag_3","tag_4", "tag_5"};
    long[] weighs = {50,40,30,20,10};
    Assertions.assertThat(tags).hasSameSizeAs(weighs);

    for (int idx = 0; idx < tags.length; idx++) {
      repository.save(userId,
              WeightedTag.builder().tagName(tags[idx]).weight(weighs[idx]).build()
      );
    }


    InputCatalogContent one = InputCatalogContent.builder()
            .contentId("1")
            .contentStatus("AVAILABLE")
            .streamId("1")
            .tags(Arrays.stream(tags).collect(Collectors.toSet()))
            .title("one")
            .build();

    InputCatalogContent two = InputCatalogContent.builder()
            .contentId("2")
            .contentStatus("AVAILABLE")
            .streamId("2")
            .tags(new HashSet<>(Arrays.asList(tags).subList(0, tags.length - 2)))
            .title("two")
            .build();

    InputCatalogContent three = InputCatalogContent.builder()
            .contentId("3")
            .contentStatus("AVAILABLE")
            .streamId("3")
            .tags(new HashSet<>(Arrays.asList(tags).subList(0, tags.length - 3)))
            .title("three")
            .build();

    InputCatalogContent four = InputCatalogContent.builder()
            .contentId("4")
            .contentStatus("AVAILABLE")
            .streamId("4")
            .tags(new HashSet<>(Arrays.asList(tags).subList(0, tags.length - 4)))
            .title("four")
            .build();

    InputCatalogContent five = InputCatalogContent.builder()
            .contentId("5")
            .contentStatus("AVAILABLE")
            .streamId("5")
            .tags(new HashSet<>(Arrays.asList(tags).subList(0, 0)))
            .title("five")
            .build();
    List<InputCatalogContent> list_5 = List.of(one, two, three, four, five);
    List<InputCatalogContent> list_4 = List.of(one, two, three, four);
    List<InputCatalogContent> list_3 = List.of(one, two, three);
    List<InputCatalogContent> list_2 = List.of(one, two);
    List<InputCatalogContent> list_1 = List.of(one);

    final String listOfTags_5 = String.join(",",tags);
    final String uri_5 = String.format("/catalog/content/exactlyWithTags?tags=%s",listOfTags_5);
    stubFor(get(urlEqualTo(uri_5))
            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(JsonUtils.ListToJson(list_5))));

    final String listOfTags_4 = String.join(",",Arrays.asList(tags).subList(0,4));
    final String uri_4 = String.format("/catalog/content/exactlyWithTags?tags=%s",listOfTags_4);
    stubFor(get(urlEqualTo(uri_4))
            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(JsonUtils.ListToJson(list_4))));

    final String listOfTags_3 = String.join(",",Arrays.asList(tags).subList(0,3));
    final String uri_3 = String.format("/catalog/content/exactlyWithTags?tags=%s",listOfTags_3);
    stubFor(get(urlEqualTo(uri_3))
            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(JsonUtils.ListToJson(list_3))));

    final String listOfTags_2 = String.join(",",Arrays.asList(tags).subList(0,2));
    final String uri_2 = String.format("/catalog/content/exactlyWithTags?tags=%s",listOfTags_2);
    stubFor(get(urlEqualTo(uri_2))
            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(JsonUtils.ListToJson(list_2))));

    final String listOfTags_1 = tags[0];
    final String uri_1 = String.format("/catalog/content/exactlyWithTags?tags=%s",listOfTags_1);
    stubFor(get(urlEqualTo(uri_1))
            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(JsonUtils.ListToJson(list_1))));


    InputUserContentFiltered filteredContent = InputUserContentFiltered.builder()
            .userId(userId)
            .contents(list_5)
            .build();
    final String filterUrl = "/categories/filter";
    stubFor(post(urlEqualTo(filterUrl))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(JsonUtils.ObjectToJson(filteredContent))
            )
    );


    List<InputCatalogContent> topRecommendationsForUser = service.getTopRecommendationsForUser(userId, 3);
    LOG.error("jlom: result " + topRecommendationsForUser);

  }


  @Test
  public void registerRecommendations() {

    final String userId = "1234";
    final String cat_tag = "cat:gold";
    InputUserActivity input = InputUserActivity.builder()
            .userId(userId)
            .operation(UserActivityOperation.WATCH)
            .tags(List.of("tag_1", cat_tag, "tag_2"))
            .build();

    RecommendationsServiceResponse response = service.register(input);
    Assertions.assertThat(response).isInstanceOf(RecommendationsServiceResponseOK.class);

    Assertions.assertThat(repository.find(userId,cat_tag)).isNull();
    Assertions.assertThat(repository.find(userId,"tag_1")).isNotNull();
    Assertions.assertThat(repository.find(userId,"tag_2")).isNotNull();
  }
}
package org.jlom.master_upm.tfm.micronaut.recommendations.model;


import com.github.tomakehurst.wiremock.WireMockServer;
import io.micronaut.test.annotation.MicronautTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jlom.master_upm.tfm.micronaut.recommendations.model.api.IRecommendationsRepository;
import org.jlom.master_upm.tfm.micronaut.recommendations.model.daos.WeightedTag;
import org.jlom.master_upm.tfm.micronaut.recommendations.utils.EmbeddedRedisServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
public class ModelTest {

  private static final Logger LOG = LoggerFactory.getLogger(ModelTest.class);

  @Inject
  private IRecommendationsRepository recommendationsRepository;

  @Inject
  EmbeddedRedisServer embeddedRedisServer;

  static WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(8080));

  @BeforeAll
  public static void init() {
    wireMockServer.start();
  }

  @AfterAll
  public  static void shudown() {
    wireMockServer.stop();
  }

  @BeforeEach
  public void setup() {
    embeddedRedisServer.start();
    //notificationListener.cleanNotifications();
    wireMockServer.resetAll();
  }

  @AfterEach
  public void tearDown() {
    //notificationListener.cleanNotifications();
    embeddedRedisServer.stop();
  }

  @Test
  public void save_tag() throws IOException {

    final String userId = "alfa";
    final String tag = "action";
    final long weight = 23;

    WeightedTag weightedTag = WeightedTag.builder()
            .tagName(tag)
            .weight(weight)
            .build();

    recommendationsRepository.save(userId, weightedTag);

    WeightedTag actual = recommendationsRepository.find(userId, weightedTag.getTagName());
    assertThat(actual).isEqualTo(weightedTag);

  }

  @Test
  public void update_tag() throws IOException {

    final String userId = "alfa";
    final String tag = "action";
    final long weight = 23;
    final long delta = 32;

    WeightedTag weightedTag = WeightedTag.builder()
            .tagName(tag)
            .weight(weight)
            .build();

    recommendationsRepository.save(userId, weightedTag);

    assertThat(recommendationsRepository.update(userId, tag, delta)).isTrue();
    WeightedTag actual = recommendationsRepository.find(userId, tag);

    assertThat(actual).isNotNull();
    assertThat(actual.getWeight()).isEqualTo(weight + delta);

  }

  @Test
  public void delete_tag() throws IOException {
    final String userId = "alfa";
    final String tag = "action";
    final long weight = 23;

    WeightedTag weightedTag = WeightedTag.builder()
            .tagName(tag)
            .weight(weight)
            .build();

    recommendationsRepository.save(userId, weightedTag);


    recommendationsRepository.delete(userId,tag);

    assertThat(recommendationsRepository.find(userId,tag)).isNull();
  }

  @Test
  public void delete_all_tag() throws IOException {

    final String userId = "alfa";

    @AllArgsConstructor
    class Node {
      public final String userId;
      public final String tag;
      public final long weight;
    }

    List<Node> nodes = List.of(
            new Node(userId, "action", 11),
            new Node(userId, "bored", 23),
            new Node(userId, "greatWar", 58)
    );


    for (var node: nodes) {
      recommendationsRepository
              .save(node.userId,WeightedTag.builder()
                      .tagName(node.tag)
                      .weight(node.weight)
                      .build()
              );
    }

    recommendationsRepository.delete(userId);

    nodes.forEach(
            node -> assertThat(recommendationsRepository.find(userId,node.tag)).isNull()
    );

  }

  @Test
  public void find_WeightedTag() throws IOException {
    final String userId = "alfa";

    @AllArgsConstructor
    class Node {
      public final String userId;
      public final String tag;
      public final long weight;
    }

    List<Node> nodes = List.of(
            new Node(userId, "action", 11),
            new Node(userId, "bored", 23),
            new Node(userId, "greatWar", 58)
    );

    for (var node : nodes) {
      recommendationsRepository
              .save(node.userId,WeightedTag.builder()
                      .tagName(node.tag)
                      .weight(node.weight)
                      .build()
              );
    }

    for (Node node : nodes) {
      WeightedTag weightedTag = recommendationsRepository.find(userId, node.tag);
      assertThat(weightedTag).isEqualTo(WeightedTag.builder()
              .tagName(node.tag)
              .weight(node.weight)
              .build());
    }
  }

  @Test
  public void find_top_for_user() throws IOException {

    final String userId = "alfa";

    @AllArgsConstructor
    @Data
    class Node {
      public final String userId;
      public final String tag;
      public final long weight;
    }

    List<Node> nodes = new java.util.ArrayList<>(List.of(
            new Node(userId, "action", 11),
            new Node(userId, "bored", 23),
            new Node(userId, "comedy", 58),
            new Node(userId, "dutch", 13),
            new Node(userId, "eclectic", 21),
            new Node(userId, "fun", 34),
            new Node(userId, "gore", 55),
            new Node(userId, "humor", 89),
            new Node(userId, "illusion", 144),
            new Node(userId, "jedis", 233)
    ));

    List<WeightedTag> expected = nodes.stream()
            .map(
                    node -> WeightedTag.builder()
                            .tagName(node.tag)
                            .weight(node.weight)
                            .build()
            )
            .sorted(Comparator.comparing(WeightedTag::getWeight).reversed())
            .collect(Collectors.toList());


    for (var node : nodes) {
      recommendationsRepository
              .save(node.userId,WeightedTag.builder()
                      .tagName(node.tag)
                      .weight(node.weight)
                      .build()
              );
    }

    List<WeightedTag> top_5 = recommendationsRepository.find(userId, 10);
    LOG.error("top(5):" + top_5);

    assertThat(top_5).containsExactly(expected.toArray(new WeightedTag[0]));

  }
}

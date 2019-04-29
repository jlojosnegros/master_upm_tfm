package org.jlom.master_upm.tfm.springboot.recommendations.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.springboot.recommendations.model.api.IRecommendationsRepository;
import org.jlom.master_upm.tfm.springboot.recommendations.model.daos.WeightedTag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class ModelTest {


  private static int redisEmbeddedServerPort = 6379;

  private RedisServer embeddedRedis = new RedisServer(redisEmbeddedServerPort);

 
  @Autowired
  private IRecommendationsRepository recommendationsRepository;

  private static final Logger LOG = LoggerFactory.getLogger(ModelTest.class);

  @Before
  public void setup() {
    embeddedRedis.start();
    String redisContainerIpAddress = "localhost";
    int redisFirstMappedPort = redisEmbeddedServerPort;
    LOG.info("-=* Redis Container running on: " + redisContainerIpAddress + ":" + redisFirstMappedPort);
  }

  @After
  public void tearDown() {
    embeddedRedis.stop();
  }

  @Test
  public void save_tag() {

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
  public void update_tag() {

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
  public void delete_tag() {
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
  public void delete_all_tag() {

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


    nodes.forEach(
            node -> recommendationsRepository
                    .save(node.userId,WeightedTag.builder()
                            .tagName(node.tag)
                            .weight(node.weight)
                            .build()
                    )
    );



    recommendationsRepository.delete(userId);

    nodes.forEach(
            node -> assertThat(recommendationsRepository.find(userId,node.tag)).isNull()
    );

  }

  @Test
  public void find_WeightedTag() {
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


    nodes.forEach(
            node -> recommendationsRepository
                    .save(node.userId,WeightedTag.builder()
                            .tagName(node.tag)
                            .weight(node.weight)
                            .build()
                    )
    );


    for (Node node : nodes) {
      WeightedTag weightedTag = recommendationsRepository.find(userId, node.tag);
      Assertions.assertThat(weightedTag).isEqualTo(WeightedTag.builder()
              .tagName(node.tag)
              .weight(node.weight)
              .build());
    }
  }

  @Test
  public void find_top_for_user() {

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


    nodes.forEach(
            node -> recommendationsRepository
                    .save(node.userId,WeightedTag.builder()
                            .tagName(node.tag)
                            .weight(node.weight)
                            .build()
                    )
    );


    List<WeightedTag> top_5 = recommendationsRepository.find(userId, 10);
    LOG.error("top(5):" + top_5);

    Assertions.assertThat(top_5).containsExactly(expected.toArray(new WeightedTag[0]));

  }
}

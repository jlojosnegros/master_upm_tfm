package org.jlom.master_upm.tfm.springboot.catalog.model;


import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class CommandContentRepositoryTest {
  @ClassRule
  public static GenericContainer redis = new GenericContainer<>("redis:5.0.4-alpine")
          .withExposedPorts(6379);

  @Autowired
  private CatalogContentRepository repository;


  private static final Logger LOG = LoggerFactory.getLogger(CommandContentRepositoryTest.class);


  @Before
  public void setup() {
    String redisContainerIpAddress = redis.getContainerIpAddress();
    Integer redisFirstMappedPort = redis.getFirstMappedPort();
    LOG.info("-=* Redis Container running on: " + redisContainerIpAddress + ":" + redisFirstMappedPort);
  }

  @TestConfiguration
  static public class RedisTestConfiguration {

    @Bean
    @Profile("test")
    @Primary
    public RedisStandaloneConfiguration redisStandaloneConfigurationTest() {
      var redisContainerIpAddress = redis.getContainerIpAddress();
      var redisFirstMappedPort = redis.getFirstMappedPort();
      LOG.info("jlom: -=* TestConfiguration Redis Container running on: " + redisContainerIpAddress + ":" + redisFirstMappedPort);
      var configuration = new RedisStandaloneConfiguration();
      configuration.setHostName(redisContainerIpAddress);
      configuration.setPort(redisFirstMappedPort);
      return configuration;
    }
  }


  @Test
  public void when_SomeContentIsSaved_then_ShouldBeInTheDataBase() {

    final long streamId = 1;
    final long contentId = 0;
    final String title = "title";
    final ContentStatus status = ContentStatus.SOON;
    final Date available = Date.from(Instant.now());
    final Set<String> tags = Set.of("tag1", "tag2");

    CatalogContent content_01 = CatalogContent.builder()
            .streamId(streamId)
            .title(title)
            .status(status)
            .available(available)
            .tags(tags)
            .build();

    CatalogContent expectedContent = CatalogContent.builder()
            .contentId(contentId)
            .streamId(streamId)
            .title(title)
            .status(status)
            .available(available)
            .tags(tags)
            .build();

    LOG.error("jlom: toInsert: " + content_01);

    Assertions.assertThat(repository).isNotNull();
    repository.save(content_01);

    CatalogContent byId = repository.findByStreamId(streamId);

    LOG.error("jlom: inserted: " + byId);
    Assertions.assertThat(byId).isEqualTo(expectedContent);

  }

  @Test
  public void given_ASavedContent_when_WeDeleteIt_thenShouldNotBeFound() {
    //given
    CatalogContent content_01 = CatalogContent.builder()
            .status(ContentStatus.SOON)
            .build();

    CatalogContent expected_content_01 = CatalogContent.builder()
            .contentId(0)
            .status(ContentStatus.SOON)
            .build();

    repository.save(content_01);
    Assertions.assertThat(repository.findAll()).contains(expected_content_01);

    Long deleted = repository.delete(content_01.getContentId());
    Assertions.assertThat(repository.findAll()).doesNotContain(expected_content_01);

    Assertions.assertThat(deleted).isNotNull();
    Assertions.assertThat(deleted).isEqualTo(1);

    deleted = repository.delete(expected_content_01.getContentId());
    Assertions.assertThat(deleted).isNotNull();
    Assertions.assertThat(deleted).isEqualTo(0);
  }

  @Test
  public void when_LookingForANonExistantContent_then() {
    CatalogContent byId = repository.findById(2);
    Assertions.assertThat(byId).isNull();
  }
}

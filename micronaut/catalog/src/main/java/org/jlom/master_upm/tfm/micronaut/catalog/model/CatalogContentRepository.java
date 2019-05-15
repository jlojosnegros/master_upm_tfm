package org.jlom.master_upm.tfm.micronaut.catalog.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.validation.Validated;
import org.jlom.master_upm.tfm.micronaut.catalog.model.api.CatalogCommandsRepository;
import org.jlom.master_upm.tfm.micronaut.catalog.model.api.CatalogQueriesRepository;
import org.jlom.master_upm.tfm.micronaut.catalog.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jlom.master_upm.tfm.micronaut.catalog.utils.JsonUtils.*;

@Singleton
@Validated
public class CatalogContentRepository implements CatalogQueriesRepository , CatalogCommandsRepository {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogContentRepository.class);


  private StatefulRedisConnection<String, String> connection;

  public CatalogContentRepository(StatefulRedisConnection<String, String> connection) {
    this.connection = connection;
  }


  @Override
  public CatalogContent findById(long contentId) {
    LOG.info("CatalogContentRepository::findById:" + contentId);
    RedisCommands<String, String> redisApi = connection.sync();
    String s = redisApi.get(String.valueOf(contentId));
    try {
      return jsonToObject(s, CatalogContent.class);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public List<CatalogContent> findWithExactlyTags(Set<String> tags) {
    LOG.info("CatalogContentRepository::findWithExactlyTags : " + tags);

    return findAll().stream()
            .filter(content -> content.getTags().containsAll(tags))
            .collect(Collectors.toList());
  }

  @Override
  public CatalogContent findByStreamId(long streamId) {
    return null;
  }

  @Override
  public List<CatalogContent> findAvailableAfter(Date date) {
    return null;
  }

  @Override
  public List<CatalogContent> findAll() {
    LOG.info("CatalogContentRepository::findAll");
    RedisCommands<String, String> redisApi = connection.sync();

    List<String> keys = redisApi.keys("*");
    LOG.info("keys:" + keys);

    List<CatalogContent> contents = keys.stream()
            .map(redisApi::get)
            .map(json -> {
              try {
                return jsonToObject(json,CatalogContent.class);
              } catch (IOException e) {
                e.printStackTrace();
                return null;
              }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    LOG.info("contents: " + contents);
    return contents;
  }

  @Override
  public List<CatalogContent> findWithStatus(ContentStatus contentStatus) {
    return null;
  }

  @Override
  public void save(CatalogContent content) {
    RedisCommands<String, String> redisApi = connection.sync();
    try {
      String strContent = ObjectToJson(content);
      LOG.error("strContent: " + strContent);
      redisApi.set(String.valueOf(content.getContentId()),strContent);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }
}

package org.jlom.master_upm.tfm.micronaut.catalog.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
@Validated
public class CatalogContentRepository implements CatalogQueriesRepository , CatalogCommandsRepository {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogContentRepository.class);


  private StatefulRedisConnection<String, String> connection;

  public CatalogContentRepository(StatefulRedisConnection<String, String> connection) {
    this.connection = connection;
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
                return JsonUtils.jsonToObject(json,CatalogContent.class);
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
  public void save(CatalogContent content) {
    RedisCommands<String, String> redisApi = connection.sync();
    try {
      String strContent = JsonUtils.ObjectToJson(content);
      LOG.error("strContent: " + strContent);
      redisApi.set(String.valueOf(content.getContentId()),strContent);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }
}

package org.jlom.master_upm.tfm.micronaut.catalog.model;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.validation.Validated;
import org.jlom.master_upm.tfm.micronaut.catalog.model.api.CatalogCommandsRepository;
import org.jlom.master_upm.tfm.micronaut.catalog.model.api.CatalogQueriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Validated
public class CatalogContentRepository implements CatalogQueriesRepository , CatalogCommandsRepository {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogContentRepository.class);


  private StatefulRedisConnection<String, CatalogContent> connection;

  public CatalogContentRepository(StatefulRedisConnection<String, CatalogContent> connection) {
    this.connection = connection;
  }


  @Override
  public List<CatalogContent> findAll() {
    LOG.info("CatalogContentRepository::findAll");
    RedisCommands<String, CatalogContent> redisApi = connection.sync();

    List<String> keys = redisApi.keys("*");
    LOG.info("keys:" + keys);

    List<CatalogContent> contents = keys.stream()
            .map(redisApi::get)
            .collect(Collectors.toList());

    LOG.info("contents: " + contents);
    return contents;
  }

  @Override
  public void save(CatalogContent content) {
    RedisCommands<String, CatalogContent> redisApi = connection.sync();
    redisApi.set(String.valueOf(content.getContentId()),content);
  }
}

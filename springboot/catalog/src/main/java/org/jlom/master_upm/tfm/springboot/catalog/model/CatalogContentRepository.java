package org.jlom.master_upm.tfm.springboot.catalog.model;

import org.jlom.master_upm.tfm.springboot.catalog.model.api.ContentCommandsRepository;
import org.jlom.master_upm.tfm.springboot.catalog.model.api.ContentQueriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class CatalogContentRepository implements ContentCommandsRepository, ContentQueriesRepository {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogContentRepository.class);

  public static final String KEY = "Catalog";

  private RedisTemplate<String,Object> redisTemplate;
  private HashOperations<String, Long, CatalogContent> hashOperations;


  @Autowired
  public CatalogContentRepository(RedisTemplate<String,Object> redisTemplate) {
    LOG.info("jlom Repo constructor");
    this.redisTemplate = redisTemplate;
  }

  @PostConstruct
  void init() {
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void save(CatalogContent content) {
    hashOperations.put(KEY,content.getContentId(),content);
  }

  @Override
  public void update(CatalogContent content) {
    hashOperations.put(KEY,content.getContentId(),content);
  }

  @Override
  public Long delete(long contentId) {
    return hashOperations.delete(KEY, contentId);
  }


  @Override
  public CatalogContent findById(long contentId) {
    return hashOperations.get(KEY, contentId);
  }

  @Override
  public List<CatalogContent> findWithExactlyTags(Set<String> tags) {
    return findAll().stream()
            .filter(content -> content.getTags().containsAll(tags))
            .collect(Collectors.toList());
  }

  @Override
  public CatalogContent findByStreamId(long streamId) {
    return findAll().stream()
            .filter(content -> content.getStreamId() == streamId)
            .findFirst()
            .orElse(null);
  }

  @Override
  public List<CatalogContent> findAvailableAfter(Date date) {
    return findAll().stream()
            .filter( content -> content.getAvailable().after(date))
            .collect(Collectors.toList());
  }

  @Override
  public Collection<CatalogContent> findAll() {
     return hashOperations.entries(KEY).values();
  }

  @Override
  public List<CatalogContent> findWithStatus(ContentStatus contentStatus) {
    Collection<CatalogContent> all = findAll();
    LOG.error("jlom all: " + all);

    LOG.error("jlom filtered: " +
            all.stream()
                    .filter(content -> content.getStatus() == contentStatus)
                    .collect(Collectors.toList())
    );

    return findAll().stream()
            .filter( content -> content.getStatus() == contentStatus)
            .collect(Collectors.toList());
  }
}

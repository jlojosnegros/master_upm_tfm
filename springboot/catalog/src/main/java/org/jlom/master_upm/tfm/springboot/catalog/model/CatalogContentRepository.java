package org.jlom.master_upm.tfm.springboot.catalog.model;

import org.joda.time.DateTime;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class CatalogContentRepository implements ContentCommandsRepository, ContentQueriesRepository {


  public static final String KEY = "Catalog";

  private RedisTemplate<String,Object> redisTemplate;
  private HashOperations<String, Long, CatalogContent> hashOperations;


  //@Autowired //jlom creo que no es necesario
  public CatalogContentRepository(RedisTemplate<String,Object> redisTemplate) {
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
  public void delete(long contentId) {
    hashOperations.delete(KEY, contentId);
  }

  @Override
  public void changeStatus(long contentId, ContentStatus status) {
    CatalogContent content = findById(contentId);
    content.setStatus(status);
    update(content);
  }

  @Override
  public void addTags(long contentId, Set<String> tags) {
    CatalogContent content = findById(contentId);
    content.getTags().addAll(tags);
    update(content);
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
  public List<CatalogContent> findAvailableAfter(DateTime datetime) {
    return findAll().stream()
            .filter( content -> content.getAvailable().isAfter(datetime))
            .collect(Collectors.toList());
  }

  @Override
  public Collection<CatalogContent> findAll() {
    return hashOperations.entries(KEY).values();

  }
}

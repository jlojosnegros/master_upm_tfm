package org.jlom.master_upm.tfm.springboot.recommendations.model;


import org.jlom.master_upm.tfm.springboot.recommendations.model.api.IRecommendationsRepository;
import org.jlom.master_upm.tfm.springboot.recommendations.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.springboot.recommendations.model.daos.WeightedTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class RecommendationsRepository implements IRecommendationsRepository {

  private static final String RECOMMENDATIONS_DATA_KEY = "RecommendationsDataKey";
  private static final String USER_COLLECTION = "User";
  private static final String USER_RANGE = "RUser";


  @Resource(name="redisTemplate")
  private HashOperations<String, String, WeightedTag> weightedTagHashOperations;

  @Resource(name="redisTemplate")
  private ZSetOperations<String, String> zSetOperations; //esto guarda key,value, score...

  private static final Logger LOG = LoggerFactory.getLogger(RecommendationsRepository.class);

  private String buildCollectionKey(String collection, String key) {
    return String.format("%s:%s:%s",
            RECOMMENDATIONS_DATA_KEY,
            collection,
            key);
  }
  private String buildUserCollectionKey(String userId) {
    return buildCollectionKey(USER_COLLECTION,userId);
  }
  private String buildRangeCollectionKey(String userId) {
    return buildCollectionKey(USER_RANGE,userId);
  }


  @Override
  public void save(String userId, WeightedTag tag) {

    String userCollectionKey = buildUserCollectionKey(userId);
    weightedTagHashOperations.put(userCollectionKey, tag.getTagName(),tag);

    // actualizando el score...
    String rangeCollectionKey = buildRangeCollectionKey(userId);
    zSetOperations.add(rangeCollectionKey, tag.getTagName(),tag.getWeight());
    //----------------------------------------------------------------------

  }

  @Override
  public boolean update(String userId, String tagName, long delta) {
    String userCollectionKey = buildUserCollectionKey(userId);

    // actualizando el score...
    String rangeCollectionKey = buildRangeCollectionKey(userId);
    zSetOperations.incrementScore(rangeCollectionKey, tagName, delta);
    //----------------------------------------------------------------------
    WeightedTag weightedTag = weightedTagHashOperations.get(userCollectionKey, tagName);
    if (null == weightedTag) {
      return false;
    }
    weightedTag.setWeight(weightedTag.getWeight() + delta);
    weightedTagHashOperations.put(userCollectionKey,tagName,weightedTag);
    return true;
  }

  @Override
  public void delete(String userId, String tagName) {

    String userCollectionKey = buildUserCollectionKey(userId);
    weightedTagHashOperations.delete(userCollectionKey,tagName);

    // actualizando el score...
    zSetOperations.remove(userCollectionKey,tagName);
    //----------------------------------------------------------------------
  }

  @Override
  public void delete(String userId) {
    final String userCollectionKey = buildUserCollectionKey(userId);
    final String rangeCollectionKey = buildRangeCollectionKey(userId);

    List<String> tagNamesForUser = weightedTagHashOperations
            .values(userCollectionKey)
            .stream()
            .map(WeightedTag::getTagName)
            .collect(Collectors.toList());


    tagNamesForUser.forEach(
            tagName -> {
              weightedTagHashOperations.delete(userCollectionKey,tagName);
              zSetOperations.remove(rangeCollectionKey,tagName);
            }
    );
  }

  @Override
  public WeightedTag find(String userId, String tagName) {

    String userCollectionKey = buildUserCollectionKey(userId);
    return weightedTagHashOperations.get(userCollectionKey, tagName);
  }

  @Override
  public List<WeightedTag> find(String userId, long top) {
    final String userCollectionKey = buildUserCollectionKey(userId);
    final String rangeCollectionKey = buildRangeCollectionKey(userId);

    final long start = 0;
    final long collectionSize = zSetOperations.size(rangeCollectionKey);
    final long end = (collectionSize > top) ? top : collectionSize;

    Set<String> orderedTags = zSetOperations.reverseRange(rangeCollectionKey, start, end);
    if (orderedTags == null) {
      return null;
    }
    List<WeightedTag> collect = orderedTags.stream()
            .map(tagName -> weightedTagHashOperations.get(userCollectionKey, tagName))
            .collect(Collectors.toList());
    return collect;
  }
}

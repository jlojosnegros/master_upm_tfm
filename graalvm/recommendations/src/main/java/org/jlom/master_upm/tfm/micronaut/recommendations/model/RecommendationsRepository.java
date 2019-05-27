package org.jlom.master_upm.tfm.micronaut.recommendations.model;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.jlom.master_upm.tfm.micronaut.recommendations.model.api.IRecommendationsRepository;
import org.jlom.master_upm.tfm.micronaut.recommendations.model.daos.WeightedTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jlom.master_upm.tfm.micronaut.recommendations.utils.JsonUtils.ObjectToJson;
import static org.jlom.master_upm.tfm.micronaut.recommendations.utils.JsonUtils.jsonToObject;

@Singleton
public class RecommendationsRepository implements IRecommendationsRepository {

  private static final Logger LOG = LoggerFactory.getLogger(RecommendationsRepository.class);

  // Hay tres colecciones:
  // 1.- USER_COLLECTION -> Son los tags vistos por cada usuario con su peso.
  //                        Tiene un wtag por cada combinacion userId+ tagname
  //
  // 2.- USER_RANGE -->  Tiene por cada usuario una lista "ordenada" de los tags vistos.
  //                    La clave es por el userId, luego usamos el tagname como field, y tiene un score.
  //
  // 3.- CONTENT --> Es una coleccion donde se tienen todos los contentIds que han sido vistos por
  //                 cualquier usuario ordenados por el numero de veces que han sido vistos.


  private static final String RECOMMENDATIONS_DATA_KEY = "RecommendationsDataKey";
  private static final String USER_COLLECTION = "User";
  private static final String USER_RANGE = "RUser";
  private static final String CONTENT_KEY = "Content";

  private StatefulRedisConnection<String, String> connection;

  public RecommendationsRepository(StatefulRedisConnection<String, String> connection) {
    this.connection = connection;
  }

  private String buildCollectionKey(String collection, String key, String tag) {
    return String.format("%s:%s:%s:%s",
            RECOMMENDATIONS_DATA_KEY,
            collection,
            key,tag);
  }

  private String buildCollectionKey(String collection, String key) {
    return String.format("%s:%s:%s",
            RECOMMENDATIONS_DATA_KEY,
            collection,
            key);
  }
  private String buildUserCollectionKey(String userId, String tagId) {
    return buildCollectionKey(USER_COLLECTION,userId,tagId);
  }
  private String buildUserCollectionKey(String userId) {
    return buildCollectionKey(USER_COLLECTION,userId,"*");
  }
  private String buildRangeCollectionKey(String userId) {
    return buildCollectionKey(USER_RANGE,userId);
  }
  private String buildContentCollectionKey() {
    return String.format("%s:%s",
            RECOMMENDATIONS_DATA_KEY,
            CONTENT_KEY
    );
  }


  @Override
  public void save(String userId, WeightedTag tag) throws IOException {
    LOG.info("RecommendationsRepository::save: userId:" + userId +",tag:" + tag);
    RedisCommands<String, String> redisApi = connection.sync();

    String userCollectionKey = buildUserCollectionKey(userId,tag.getTagName());
    redisApi.set(userCollectionKey,ObjectToJson(tag));

    // actualizando el score...
    String rangeCollectionKey = buildRangeCollectionKey(userId);
    //key, score, member --> collection, score , tagname
    // store the tags ordered by weight for each user
    redisApi.zadd(rangeCollectionKey,tag.getWeight(),tag.getTagName());

    //----------------------------------------------------------------------

  }

  @Override
  public boolean update(String userId, String tagName, long delta) {
    LOG.info("RecommendationsRepository::update: userId:"+ userId
            +",tagName:" + tagName
            +", delta:" + delta);

    RedisCommands<String, String> redisApi = connection.sync();

    String userCollectionKey = buildUserCollectionKey(userId,tagName);
    String jsonData = redisApi.get(userCollectionKey);
    if (null == jsonData) {
      return false;
    }
    try {
      WeightedTag weightedTag = jsonToObject(jsonData, WeightedTag.class);
      weightedTag.setWeight(weightedTag.getWeight() + delta);
      redisApi.set(userCollectionKey,ObjectToJson(weightedTag));

      // actualizando el score...
      String rangeCollectionKey = buildRangeCollectionKey(userId);
      redisApi.zincrby(rangeCollectionKey,delta,tagName);


      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public void delete(String userId, String tagName) {
    LOG.info("RecommendationsRepository::delete: userId:"+ userId
            +",tagName:" + tagName
            );
    RedisCommands<String, String> redisApi = connection.sync();

    String userCollectionKey = buildUserCollectionKey(userId,tagName);
    redisApi.del(userCollectionKey);
    redisApi.zrem(buildRangeCollectionKey(userId),tagName);

  }

  @Override
  public void delete(String userId) {
    LOG.info("RecommendationsRepository::delete: userId:"+ userId);
    RedisCommands<String, String> redisApi = connection.sync();

    final String userCollectionKey = buildUserCollectionKey(userId);
    final String rangeCollectionKey = buildRangeCollectionKey(userId);


    List<String> tagNamesForUser = redisApi.keys(userCollectionKey)
            .stream()
            .map(redisApi::get)
            .map(jsonData -> {
              try {
                return jsonToObject(jsonData, WeightedTag.class);
              } catch (IOException e) {
                e.printStackTrace();
                return null;
              }
            })
            .filter(Objects::nonNull)
            .map(WeightedTag::getTagName)
            .collect(Collectors.toList());

    tagNamesForUser.forEach(
            tagName -> {
              redisApi.del(buildUserCollectionKey(userId,tagName));
              redisApi.zrem(buildContentCollectionKey(),tagName);
            }
    );
  }

  @Override
  public void addVisualization(String contentId) {
    LOG.info("RecommendationsRepository::addVisualization: contentId:"+ contentId);
    RedisCommands<String, String> redisApi = connection.sync();
    redisApi.zincrby(buildContentCollectionKey(),1L,contentId);

  }

  @Override
  public WeightedTag find(String userId, String tagName) {
    LOG.info("RecommendationsRepository::find: userId:"+ userId +",tagName:" + tagName);
    RedisCommands<String, String> redisApi = connection.sync();

    String userCollectionKey = buildUserCollectionKey(userId,tagName);
    String jsonData = redisApi.get(userCollectionKey);
    if(null == jsonData) {
      return null;
    }
    try {
      return jsonToObject(jsonData,WeightedTag.class);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public List<WeightedTag> find(String userId, long top) {
    LOG.info("RecommendationsRepository::find: userId:"+ userId +",top:" + top);
    RedisCommands<String, String> redisApi = connection.sync();

    final String userCollectionKey = buildUserCollectionKey(userId);
    final String rangeCollectionKey = buildRangeCollectionKey(userId);


    final long start = 0;
    final long collectionSize = redisApi.keys(userCollectionKey).size();
    final long end = (collectionSize > top) ? top : collectionSize;

    List<String> orderedTags = redisApi.zrevrange(rangeCollectionKey, start, end);
    if (orderedTags == null) {
      return null;
    }
    List<WeightedTag> collect = orderedTags.stream()
            .map(tagName ->
            {
              LOG.error("jlom: tagName:" + tagName);
              return redisApi.get(buildUserCollectionKey(userId,tagName));
            })
            .map( jsonData -> {
              try {
                //if (null == jsonData) return null;
                LOG.error("jlom: jsonData:" + jsonData);
                if (null == jsonData) return null;
                return jsonToObject(jsonData,WeightedTag.class);
              } catch (IOException e) {
                e.printStackTrace();
                return null;
              }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    return collect;
  }

  @Override
  public Set<String> listMostViewed(long top) {
    LOG.info("RecommendationsRepository::listMostViewed: top:" + top);
    RedisCommands<String, String> redisApi = connection.sync();

    final String key = buildContentCollectionKey();

    List<String> tags = redisApi.zrevrange(key, 0, top);
    return new HashSet<>(tags);
  }
}

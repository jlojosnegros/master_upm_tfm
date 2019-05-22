package org.jlom.master_upm.tfm.micronaut.apigw.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.jlom.master_upm.tfm.micronaut.apigw.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
public class UsersRepository {

  private static final Logger LOG = LoggerFactory.getLogger(UsersRepository.class);
  private static final String UserModelCollection = "UMC";
  private static final String AnyUsername = "*";
  private StatefulRedisConnection<String, String> connection;



  public UsersRepository(StatefulRedisConnection<String, String> connection) {
    this.connection = connection;
  }

  public UserModel findByUsername(String username) {
    LOG.info("UsersRepository::findByUsername username:" + username);
    RedisCommands<String, String> redisApi = connection.sync();

    try {
      return JsonUtils.jsonToObject(redisApi.get(buildKey(username)), UserModel.class);
    } catch (IOException | NullPointerException e) {
      e.printStackTrace();
      LOG.error("UsersRepository::findByUsername exception: " + e.getMessage());
      return null;
    }
  }

  public Iterable<UserModel> findAll() {
    LOG.info("UsersRepository::findAll");
    RedisCommands<String, String> redisApi = connection.sync();

//    String anyUserKey = buildKey(AnyUsername);
//
//    List<String> keys = redisApi.keys(anyUserKey);
//
//    LOG.error("anyUserKey:" + anyUserKey);
//    LOG.error("keys:" + keys);
//    for (var key : keys) {
//
//    }

    return redisApi.keys(buildKey(AnyUsername))
            .stream()
            .map(
                    key -> {
                      try {
                        return JsonUtils.jsonToObject(redisApi.get(key),UserModel.class);
                      } catch (IOException e) {
                        return null;
                      }
                    }
            )
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableList());
  }

  public void save(UserModel userModel) throws JsonProcessingException {
    LOG.info("UsersRepository::save userModel:" + userModel);
    RedisCommands<String, String> redisApi = connection.sync();

    String key = buildKey(userModel.getUsername());
    String jsonData = JsonUtils.ObjectToJson(userModel);
    LOG.info("key: " + key);
    LOG.info("data: " + jsonData);
    redisApi.set(key,jsonData);
  }




  private String buildKey(final String username) {
    return String.format("%s:%s"
            , UserModelCollection
            , username);
  }
}

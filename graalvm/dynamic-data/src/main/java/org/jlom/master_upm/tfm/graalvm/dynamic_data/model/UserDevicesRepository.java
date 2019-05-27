package org.jlom.master_upm.tfm.graalvm.dynamic_data.model;



import com.fasterxml.jackson.core.JsonProcessingException;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.validation.Validated;
import org.jlom.master_upm.tfm.graalvm.dynamic_data.model.api.IUserDevicesRepository;
import org.jlom.master_upm.tfm.graalvm.dynamic_data.model.daos.UserDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.jlom.master_upm.tfm.graalvm.dynamic_data.utils.JsonUtils.ObjectToJson;
import static org.jlom.master_upm.tfm.graalvm.dynamic_data.utils.JsonUtils.jsonToObject;

@Singleton
@Validated
public class UserDevicesRepository implements IUserDevicesRepository {

  private static final String USER_DEVICES_KEY = "UserDevicesKey";
  private static final String DEVICE_USER_KEY = "DeviceUserKey";

  private StatefulRedisConnection<String, String> connection;


  private static final Logger LOG = LoggerFactory.getLogger(UserDevicesRepository.class);

  public UserDevicesRepository(StatefulRedisConnection<String, String> connection) {
    this.connection = connection;
  }


  private String userDeviceKey(final UserDevice userDevice) {
    return userDeviceKey(userDevice.getUserId());
  }
  private String userDeviceKey(final long userId) {
    return String.format("%s:%d", USER_DEVICES_KEY, userId);
  }

  private String deviceUserKey(final long deviceId) {
    return String.format("%s:%d", DEVICE_USER_KEY, deviceId);
  }

  @Override
  public boolean add(UserDevice userDevice) {
    LOG.info("UserDevicesRepository::add: " + userDevice);

    RedisCommands<String, String> redisApi = connection.sync();
    String strContent;

    try {
      strContent = ObjectToJson(userDevice);
      LOG.error("strContent: " + strContent);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return false;
    }


    Boolean retVal = redisApi.setnx(userDeviceKey(userDevice), strContent);
    if (retVal) {
      userDevice.getDevices()
              .forEach(deviceId -> redisApi
                      .set(deviceUserKey(deviceId),
                              String.valueOf(userDevice.getUserId())
                      )
              );
    }
    return retVal;
  }

  @Override
  public Long delete(long userId) {
    LOG.info("UserDevicesRepository::delete: " + userId);

    RedisCommands<String, String> redisApi = connection.sync();
    String jsonData = redisApi.get(userDeviceKey(userId));
    if (null != jsonData) {

      UserDevice userDevice;
      try {
        userDevice = jsonToObject(jsonData, UserDevice.class);
      } catch (IOException e) {
        e.printStackTrace();
        return 0L;
      }

      userDevice.getDevices().forEach(
                deviceId -> redisApi.del(deviceUserKey(deviceId))
        );
      return redisApi.del(userDeviceKey(userId));
    }
    return 0L;
  }

  @Override
  public boolean update(UserDevice userDevice) {
    LOG.info("UserDevicesRepository::update: " + userDevice);


    RedisCommands<String, String> redisApi = connection.sync();
    String jsonData = redisApi.get(userDeviceKey(userDevice));
    if (null == jsonData) {
      return false;
    }

    UserDevice before;
    String after;
    try {
      before = jsonToObject(jsonData, UserDevice.class);
      after = ObjectToJson(userDevice);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    before.getDevices().forEach(
            deviceId -> redisApi.del(deviceUserKey(deviceId))
    );


    redisApi.set(userDeviceKey(userDevice),after);
    userDevice.getDevices().forEach(
            deviceId -> redisApi.set(deviceUserKey(deviceId),
                    String.valueOf(userDevice.getUserId())
                    )
    );
    return true;
  }

  @Override
  public UserDevice findByUserId(long userId) {
    LOG.info("UserDevicesRepository::findByUserId: " + userId);

    RedisCommands<String, String> redisApi = connection.sync();
    String jsonData = redisApi.get(userDeviceKey(userId));
    if (null == jsonData) return null;

    try {
      return jsonToObject(jsonData,UserDevice.class);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public UserDevice findByDeviceId(long deviceId) {
    LOG.info("UserDevicesRepository::findByDeviceId: " + deviceId);

    RedisCommands<String, String> redisApi = connection.sync();
    String userIdStr = redisApi.get(deviceUserKey(deviceId));
    if ( null == userIdStr) {
      return null;
    }

    String jsonData = redisApi.get(userDeviceKey(Long.parseLong(userIdStr)));
    try {
      return jsonToObject(jsonData,UserDevice.class);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public List<UserDevice> listAllUsers() {
    LOG.info("UserDevicesRepository::listAllUsers");
    RedisCommands<String, String> redisApi = connection.sync();

    List<String> keys = redisApi.keys("*");
    LOG.info("keys:" + keys);

    List<UserDevice> contents = keys.stream()
            .map(redisApi::get)
            .map(json -> {
              try {
                return jsonToObject(json,UserDevice.class);
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
}

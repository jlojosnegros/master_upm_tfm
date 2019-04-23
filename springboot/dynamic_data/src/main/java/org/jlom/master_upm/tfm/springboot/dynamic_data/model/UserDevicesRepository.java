package org.jlom.master_upm.tfm.springboot.dynamic_data.model;

import org.jlom.master_upm.tfm.springboot.dynamic_data.model.api.IUserDevicesRepository;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos.UserDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class UserDevicesRepository implements IUserDevicesRepository {

  private static final String USER_DEVICES_KEY = "UserDevicesKey";
  private static final String DEVICE_USER_KEY = "DeviceUserKey";

  @Resource(name="redisTemplate")
  private HashOperations<String, Long, UserDevice> userDeviceHashOperations;

  @Resource(name="redisTemplate")
  private HashOperations<String, Long, Long> deviceUserHashOperations;

  private static final Logger LOG = LoggerFactory.getLogger(UserDevicesRepository.class);



  @Override
  public boolean add(UserDevice userDevice) {
    Boolean inserted = userDeviceHashOperations.putIfAbsent(USER_DEVICES_KEY, userDevice.getUserId(), userDevice);
//    if (inserted && !userDevice.getUser().isEmpty()){
    if(inserted) {
      userDevice.getDevices()
              .forEach( deviceId -> deviceUserHashOperations.put(DEVICE_USER_KEY,
                      deviceId,
                      userDevice.getUserId())
              );
    }
    return inserted;
  }

  @Override
  public Long delete(long userId) {

    UserDevice byUserId = userDeviceHashOperations.get(USER_DEVICES_KEY,userId);
    if (null != byUserId) {
      byUserId.getDevices().forEach(
              deviceId -> deviceUserHashOperations.delete(DEVICE_USER_KEY,deviceId)
      );
    }
    return userDeviceHashOperations.delete(USER_DEVICES_KEY, userId);
  }

  @Override
  public boolean update(UserDevice userDevice) {
    UserDevice before = userDeviceHashOperations.get(USER_DEVICES_KEY, userDevice.getUserId());
    if (null == before) {
      return false;
    }
    //delete all the old values
    before.getDevices().forEach(
            deviceId -> deviceUserHashOperations.delete(DEVICE_USER_KEY, deviceId)
    );

    userDeviceHashOperations.put(USER_DEVICES_KEY, userDevice.getUserId(), userDevice);

    //insert new values
    userDevice.getDevices().forEach(
            deviceId -> deviceUserHashOperations.put(DEVICE_USER_KEY,deviceId,userDevice.getUserId())
    );
    return true;
  }

  @Override
  public UserDevice findByUserId(long userId) {
    return userDeviceHashOperations.get(USER_DEVICES_KEY, userId);
  }

  @Override
  public UserDevice findByDeviceId(long deviceId) {
    Long userId = deviceUserHashOperations.get(DEVICE_USER_KEY, deviceId);
    if (null == userId) {
      return null;
    }
    return userDeviceHashOperations.get(USER_DEVICES_KEY, userId);
  }

  @Override
  public List<UserDevice> listAllUsers() {
    return userDeviceHashOperations.values(USER_DEVICES_KEY);
  }
}

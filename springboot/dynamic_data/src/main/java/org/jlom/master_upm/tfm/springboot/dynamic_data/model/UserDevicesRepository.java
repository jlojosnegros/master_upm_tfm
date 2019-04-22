package org.jlom.master_upm.tfm.springboot.dynamic_data.model;

import org.jlom.master_upm.tfm.springboot.dynamic_data.model.api.IUserDevicesRepository;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos.UserDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class UserDevicesRepository implements IUserDevicesRepository {

  private static final String KEY = "UserDevicesKey";

  @Resource(name="redisTemplate")
  private HashOperations<String, Long, UserDevice> hashOperations;

  private static final Logger LOG = LoggerFactory.getLogger(UserDevicesRepository.class);



  @Override
  public boolean add(UserDevice userDevice) {
    return hashOperations.putIfAbsent(KEY, userDevice.getUserId(), userDevice);
  }

  @Override
  public Long delete(long userId) {
    return hashOperations.delete(KEY,userId);
  }

  @Override
  public boolean update(UserDevice userDevice) {
    if (null != hashOperations.get(KEY, userDevice.getUserId()) ) {
      hashOperations.put(KEY, userDevice.getUserId(), userDevice);
      return true;
    }
    return false;
  }

  @Override
  public UserDevice findByUserId(long userId) {
    return hashOperations.get(KEY, userId);
  }
}

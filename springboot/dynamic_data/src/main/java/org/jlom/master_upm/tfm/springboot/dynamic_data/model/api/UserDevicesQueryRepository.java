package org.jlom.master_upm.tfm.springboot.dynamic_data.model.api;

import org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos.UserDevice;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDevicesQueryRepository {

  UserDevice findByUserId(long userId);
  UserDevice findByDeviceId(long deviceId);
}

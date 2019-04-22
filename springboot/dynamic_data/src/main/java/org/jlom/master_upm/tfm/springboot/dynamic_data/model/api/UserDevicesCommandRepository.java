package org.jlom.master_upm.tfm.springboot.dynamic_data.model.api;

import org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos.UserDevice;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDevicesCommandRepository {

  boolean add(UserDevice userDevice);
  boolean update(UserDevice userDevice);
  Long delete(long userId);
}

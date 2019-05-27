package org.jlom.master_upm.tfm.graalvm.dynamic_data.model.api;


import org.jlom.master_upm.tfm.graalvm.dynamic_data.model.daos.UserDevice;

public interface UserDevicesCommandRepository {

  boolean add(UserDevice userDevice);
  boolean update(UserDevice userDevice);
  Long delete(long userId);
}

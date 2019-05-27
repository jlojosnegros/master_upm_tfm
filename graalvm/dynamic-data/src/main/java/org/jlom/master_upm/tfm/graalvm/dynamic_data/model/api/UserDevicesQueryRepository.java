package org.jlom.master_upm.tfm.graalvm.dynamic_data.model.api;


import org.jlom.master_upm.tfm.graalvm.dynamic_data.model.daos.UserDevice;

import java.util.List;

public interface UserDevicesQueryRepository {

  UserDevice findByUserId(long userId);
  UserDevice findByDeviceId(long deviceId);
  List<UserDevice> listAllUsers();
}

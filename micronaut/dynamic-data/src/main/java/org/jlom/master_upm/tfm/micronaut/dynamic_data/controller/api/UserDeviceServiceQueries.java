package org.jlom.master_upm.tfm.micronaut.dynamic_data.controller.api;



import org.jlom.master_upm.tfm.micronaut.dynamic_data.model.daos.UserDevice;

import java.util.List;
import java.util.Optional;

public interface UserDeviceServiceQueries {

  UserDevice getUser(long userId);
  Optional<Long> getUserForDevice(long deviceId);
  List<UserDevice> listAll();

}

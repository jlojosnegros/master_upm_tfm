package org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api;

import org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos.UserDevice;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserDeviceServiceQueries {

  Set<Long> getDevices(long userId);
  Optional<Long> getUserForDevice(long deviceId);
  List<UserDevice> listAll();

}

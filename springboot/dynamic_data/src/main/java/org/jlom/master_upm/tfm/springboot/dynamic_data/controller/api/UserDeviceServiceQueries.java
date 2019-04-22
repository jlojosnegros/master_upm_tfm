package org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api;

import java.util.Optional;
import java.util.Set;

public interface UserDeviceServiceQueries {

  Set<Long> getDevices(long userId);
  Optional<Long> getUserForDevice(long deviceId);

}

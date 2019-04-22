package org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api;

import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponse;

import java.util.Set;

public interface UserDeviceServiceCommands {

  UserDeviceServiceResponse createUser(long userId);
  UserDeviceServiceResponse addDevicesToUser(long usedId, Set<Long> deviceIds );
  UserDeviceServiceResponse removeDevicesFromUser(long userId, Set<Long> deviceIds);
}

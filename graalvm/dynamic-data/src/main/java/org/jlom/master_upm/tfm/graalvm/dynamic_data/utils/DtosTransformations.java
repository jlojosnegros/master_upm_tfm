package org.jlom.master_upm.tfm.graalvm.dynamic_data.utils;



import org.jlom.master_upm.tfm.graalvm.dynamic_data.model.daos.UserDevice;
import org.jlom.master_upm.tfm.graalvm.dynamic_data.view.api.dtos.InputUserDevice;

import java.util.Set;
import java.util.stream.Collectors;

public class DtosTransformations {

  public static UserDevice viewToService(final InputUserDevice inputUserDevice) {
    final long userId = Long.parseLong(inputUserDevice.getUserId());
    final Set<Long> devices = inputUserDevice.getDevices().stream().map(Long::parseLong).collect(Collectors.toSet());
    return new UserDevice(userId,devices);
  }

  public static InputUserDevice serviceToView(final UserDevice userDevice) {

    return InputUserDevice.builder()
            .userId(String.valueOf(userDevice.getUserId()))
            .devices(userDevice.getDevices().stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet()))
            .build();
  }
}
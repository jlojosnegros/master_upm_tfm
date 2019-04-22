package org.jlom.master_upm.tfm.springboot.dynamic_data.controller;

import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.UserDeviceServiceCommands;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.UserDeviceServiceQueries;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponse;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseFailureInteralError;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.api.IUserDevicesRepository;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos.UserDevice;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserDeviceService implements UserDeviceServiceCommands, UserDeviceServiceQueries {


  private final IUserDevicesRepository repository;

  public UserDeviceService(IUserDevicesRepository repository) {
    this.repository = repository;
  }

  @Override
  public UserDeviceServiceResponse createUser(long userId) {
    UserDevice built = UserDevice.builder().userId(userId).build();

    boolean added = repository.add(built);
    if ( added) {
      UserDevice inserted = repository.findByUserId(userId);
      return new UserDeviceServiceResponseOK(inserted);
    } else {
      return new UserDeviceServiceResponseFailureInvalidInputParameter("userId", userId);
    }
  }

  @Override
  public UserDeviceServiceResponse addDevicesToUser(long userId, Set<Long> deviceIds) {

    UserDevice byUserId = repository.findByUserId(userId);
    if (null == byUserId) {
      return new UserDeviceServiceResponseFailureNotFound("userId", userId);
    }

    for (var deviceId : deviceIds) {
      byUserId.addDevice(deviceId);
    }
    return updateUserDevice(byUserId);
  }

  private UserDeviceServiceResponse updateUserDevice(UserDevice byUserId) {
    boolean update = repository.update(byUserId);
    if (!update) {
      return new UserDeviceServiceResponseFailureInteralError("Unable to update: " + byUserId);
    }
    return new UserDeviceServiceResponseOK(byUserId);
  }

  @Override
  public UserDeviceServiceResponse removeDevicesFromUser(long userId, Set<Long> deviceIds) {
    UserDevice byUserId = repository.findByUserId(userId);
    if (null == byUserId) {
      return new UserDeviceServiceResponseFailureNotFound("userId", userId);
    }

    for( var deviceId: deviceIds) {
      byUserId.removeDevice(deviceId);
    }

    return updateUserDevice(byUserId);
  }

  @Override
  public Set<Long> getDevices(long userId) {
    UserDevice byUserId = repository.findByUserId(userId);
    if (null == byUserId) {
      return Set.of();
    }
    return byUserId.getDevices();
  }

  @Override
  public Optional<Long> getUserForDevice(long deviceId) {
    UserDevice byDeviceId = repository.findByDeviceId(deviceId);
    if (null == byDeviceId) {
      return Optional.empty();
    }
    return Optional.of(byDeviceId.getUserId());
  }
}

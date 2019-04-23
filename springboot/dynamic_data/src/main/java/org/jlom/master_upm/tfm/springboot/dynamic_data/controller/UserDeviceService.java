package org.jlom.master_upm.tfm.springboot.dynamic_data.controller;

import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.UserDeviceServiceCommands;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.UserDeviceServiceQueries;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponse;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.api.IUserDevicesRepository;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos.UserDevice;
import org.springframework.stereotype.Service;

import java.util.List;
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
    return createUser(userId,null);

  }

  private boolean inconsistentDeviceIds(long userId, Set<Long> deviceIds) {
    if (null == deviceIds) return false;

    return deviceIds.stream()
            .map(repository::findByDeviceId)
            .anyMatch(byDeviceId -> (null != byDeviceId) && (userId != byDeviceId.getUserId()));
  }

  private boolean addEntity(long userId, Set<Long> deviceIds) {
    UserDevice built = UserDevice.builder()
            .userId(userId)
            .devices(deviceIds)
            .build();

    return repository.add(built);
  }

  @Override
  public UserDeviceServiceResponse createUser(long userId, Set<Long> deviceIds) {

    if (inconsistentDeviceIds(userId, deviceIds)) {
      return new UserDeviceServiceResponseFailureInvalidInputParameter("One device can only belong to one user",
              "deviceIds",
              deviceIds);
    }

    if (addEntity(userId, deviceIds)) {
      UserDevice inserted = repository.findByUserId(userId);
      return new UserDeviceServiceResponseOK(inserted);
    } else {
      return new UserDeviceServiceResponseFailureInvalidInputParameter("userId", userId);
    }

  }



  @Override
  public UserDeviceServiceResponse addDevicesToUser(long userId, Set<Long> deviceIds) {

    if (inconsistentDeviceIds(userId, deviceIds)) {
      return new UserDeviceServiceResponseFailureInvalidInputParameter("One device can only belong to one user",
              "deviceIds",
              deviceIds);
    }

    addEntity(userId, deviceIds);
    UserDevice byUserId = repository.findByUserId(userId);

    for (var deviceId : deviceIds) {
      byUserId.addDevice(deviceId);
    }
    return updateUserDevice(byUserId);
  }

  private UserDeviceServiceResponse updateUserDevice(UserDevice userDevice) {
    if (inconsistentDeviceIds(userDevice.getUserId(), userDevice.getDevices())) {
      return new UserDeviceServiceResponseFailureInvalidInputParameter("One device can only belong to one user",
              "deviceIds",
              userDevice.getDevices());
    }
    boolean update = repository.update(userDevice);
    if (!update) {
      return new UserDeviceServiceResponseFailureInternalError("Unable to update: " + userDevice);
    }
    return new UserDeviceServiceResponseOK(userDevice);
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
  public UserDevice getUser(long userId) {
    return repository.findByUserId(userId);
  }

  @Override
  public Optional<Long> getUserForDevice(long deviceId) {
    UserDevice byDeviceId = repository.findByDeviceId(deviceId);
    if (null == byDeviceId) {
      return Optional.empty();
    }
    return Optional.of(byDeviceId.getUserId());
  }

  @Override
  public List<UserDevice> listAll() {
    return repository.listAllUsers();
  }
}

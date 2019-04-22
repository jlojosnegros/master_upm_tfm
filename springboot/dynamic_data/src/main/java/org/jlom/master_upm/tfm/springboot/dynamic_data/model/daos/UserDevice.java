package org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Builder
public class UserDevice implements Serializable {

  private static final long serialVersionUID = 1L;

  @Getter
  @Setter
  private long userId;
  private Set<Long> devices;


//  public UserDevice(final long userId) {
//    this.userId = userId;
//    this.devices = new HashSet<>();
//  }

  public UserDevice(final long userId, Set<Long> devices) {
    this.userId = userId;
    this.devices = devices;
  }

  public void addDevice(final long deviceId) {
    devices.add(deviceId);
  }

  public void removeDevice(final long deviceId) {
    devices.remove(deviceId);
  }

  public boolean hasDevice(final long deviceId) {
    return devices.contains(deviceId);
  }

  public Set<Long> getDevices() {
    return Collections.unmodifiableSet(devices);
  }

}

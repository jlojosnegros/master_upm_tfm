package org.jlom.master_upm.tfm.micronaut.dynamic_data.model.daos;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@ToString
public class UserDevice implements Serializable {

  private static final long serialVersionUID = 1L;

  @Getter
  @Setter
  private long userId;

  private Set<Long> devices;



  public UserDevice(final long userId, @NotNull Set<Long> devices) {
    this.userId = userId;
    this.devices = new HashSet<>();
    if (null != devices) {
      this.devices.addAll(devices);
    }
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


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserDevice that = (UserDevice) o;
    return userId == that.userId &&
            Objects.equals(devices, that.devices);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId);
  }
}

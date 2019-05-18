package org.jlom.master_upm.tfm.micronaut.stream_control.model.api;


import org.jlom.master_upm.tfm.micronaut.stream_control.model.daos.StreamControlData;

public interface StreamControlRepositoryQueries {

  StreamControlData findStreamingRunning(long userId, long deviceId);
  StreamControlData isUserRunning(long userId);
  StreamControlData isDeviceRunning(long deviceId);
}

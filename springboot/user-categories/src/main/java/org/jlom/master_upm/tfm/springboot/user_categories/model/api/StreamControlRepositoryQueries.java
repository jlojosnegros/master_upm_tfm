package org.jlom.master_upm.tfm.springboot.user_categories.model.api;


import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.StreamControlData;
import org.springframework.stereotype.Repository;

@Repository
public interface StreamControlRepositoryQueries {

  StreamControlData findStreamingRunning(long userId, long deviceId);
  StreamControlData isUserRunning(long userId);
  StreamControlData isDeviceRunning(long deviceId);
}

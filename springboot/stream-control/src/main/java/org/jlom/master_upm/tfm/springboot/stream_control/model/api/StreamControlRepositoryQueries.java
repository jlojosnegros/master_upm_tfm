package org.jlom.master_upm.tfm.springboot.stream_control.model.api;


import org.jlom.master_upm.tfm.springboot.stream_control.model.daos.StreamControlData;
import org.springframework.stereotype.Repository;

@Repository
public interface StreamControlRepositoryQueries {

  StreamControlData findStreamingRunning(long userId, long deviceId);
}

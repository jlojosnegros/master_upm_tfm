package org.jlom.master_upm.tfm.graalvm.stream_control.model.api;


import org.jlom.master_upm.tfm.graalvm.stream_control.model.daos.StreamControlData;

public interface StreamControlRepositoryCommands {
  void save(StreamControlData toInsert);
  boolean update(StreamControlData deviceRunning);
}

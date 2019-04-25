package org.jlom.master_upm.tfm.springboot.user_categories.model.api;


import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.StreamControlData;
import org.springframework.stereotype.Repository;

@Repository
public interface StreamControlRepositoryCommands {
  void save(StreamControlData toInsert);

  boolean update(StreamControlData deviceRunning);
}

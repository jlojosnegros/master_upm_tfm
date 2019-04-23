package org.jlom.master_upm.tfm.springboot.stream_control.model;


import org.jlom.master_upm.tfm.springboot.stream_control.model.api.IStreamControlRepository;
import org.jlom.master_upm.tfm.springboot.stream_control.model.daos.StreamControlData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class StreamControlRepository implements IStreamControlRepository {

  private static final String STREAMCONTROL_DATA_KEY = "StreamControlDataKey";


  @Resource(name="redisTemplate")
  private HashOperations<String, Long, StreamControlData> streamControlDataHashOperations;


  private static final Logger LOG = LoggerFactory.getLogger(StreamControlRepository.class);



  
}

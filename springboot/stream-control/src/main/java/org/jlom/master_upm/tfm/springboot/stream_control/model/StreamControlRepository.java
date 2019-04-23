package org.jlom.master_upm.tfm.springboot.stream_control.model;


import org.jlom.master_upm.tfm.springboot.stream_control.model.api.IStreamControlRepository;
import org.jlom.master_upm.tfm.springboot.stream_control.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.springboot.stream_control.model.daos.StreamStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class StreamControlRepository implements IStreamControlRepository {

  private static final String STREAMCONTROL_DATA_KEY = "StreamControlDataKey";


  @Resource(name="redisTemplate")
  private HashOperations<String, Long, StreamControlData> streamControlDataHashOperations;


  private static final Logger LOG = LoggerFactory.getLogger(StreamControlRepository.class);


  private String buildUserCollectionKey(long userId) {
    return String.format("%s:%s", STREAMCONTROL_DATA_KEY, String.valueOf(userId));
  }

  @Override
  public StreamControlData findStreamingRunning(long userId, long deviceId) {

    // use a different collection for each user
    final String userCollectionKey = buildUserCollectionKey(userId);
    // and deviceId as key inside each collection.
    StreamControlData streamControlData = streamControlDataHashOperations.get(userCollectionKey, deviceId);
    if ( (null == streamControlData) || (streamControlData.getStatus() == StreamStatus.DONE)) {
      return null;
    }
    return streamControlData;
  }

  @Override
  public void save(StreamControlData toInsert) {
    final String userCollectionKey = buildUserCollectionKey(toInsert.getUserId());

    streamControlDataHashOperations.put(userCollectionKey,toInsert.getDeviceId(), toInsert);
  }
}

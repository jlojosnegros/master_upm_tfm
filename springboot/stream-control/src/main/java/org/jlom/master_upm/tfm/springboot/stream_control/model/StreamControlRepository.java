package org.jlom.master_upm.tfm.springboot.stream_control.model;


import org.jlom.master_upm.tfm.springboot.stream_control.model.api.IStreamControlRepository;
import org.jlom.master_upm.tfm.springboot.stream_control.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.springboot.stream_control.model.daos.StreamStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class StreamControlRepository implements IStreamControlRepository {

  private static final String STREAMCONTROL_DATA_KEY = "StreamControlDataKey";
  private static final String USER_COLLECTION = "User";
  private static final String DEVICE_COLLECTION = "Dev";


  @Resource(name="redisTemplate")
  private HashOperations<String, Long, StreamControlData> streamControlDataHashOperations;


  private static final Logger LOG = LoggerFactory.getLogger(StreamControlRepository.class);

  private String buildCollectionKey(String collection, long key) {
    return String.format("%s:%s:%s",
            STREAMCONTROL_DATA_KEY,
            collection,
            String.valueOf(key));
  }
  private String buildUserCollectionKey(long userId) {
    return buildCollectionKey(USER_COLLECTION,userId);
  }

  private String buildDeviceCollectionKey(long deviceId) {
    return buildCollectionKey(DEVICE_COLLECTION,deviceId);
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
  public StreamControlData isUserRunning(long userId) {
    final String userCollectionKey = buildUserCollectionKey(userId);

    List<StreamControlData> values = streamControlDataHashOperations.values(userCollectionKey);
    List<StreamControlData> running = values.stream()
            .filter(data -> data.getStatus() == StreamStatus.RUNNING)
            .collect(Collectors.toList());

    if (running.isEmpty()) {
      return null;
    } else if (running.size() > 1) {
      LOG.error("ERROR!!! Should NOT be more than one device running for each user:" + running);
      return null;
    } else {
      return running.get(0);
    }
  }

  @Override
  public StreamControlData isDeviceRunning(long deviceId) {
    final String deviceCollectionKey = buildDeviceCollectionKey(deviceId);

    List<StreamControlData> values = streamControlDataHashOperations.values(deviceCollectionKey);
    List<StreamControlData> running = values.stream()
            .filter(data -> data.getStatus() == StreamStatus.RUNNING)
            .collect(Collectors.toList());

    if (running.isEmpty()) {
      return null;
    } else if (running.size() > 1) {
      LOG.error("ERROR!!! Should NOT be more than one user running for each device:" + running);
      return null;
    } else {
      return running.get(0);
    }
  }

  @Override
  public void save(StreamControlData toInsert) {
    final String userCollectionKey = buildUserCollectionKey(toInsert.getUserId());
    streamControlDataHashOperations.put(userCollectionKey,toInsert.getDeviceId(), toInsert);

    final String deviceCollectionKey = buildDeviceCollectionKey(toInsert.getDeviceId());
    streamControlDataHashOperations.put(deviceCollectionKey,toInsert.getUserId(),toInsert);
  }

  @Override
  public boolean update(StreamControlData deviceRunning) {

    final String userCollectionKey = buildUserCollectionKey(deviceRunning.getUserId());
    StreamControlData dataFromUserCollection = streamControlDataHashOperations.get(userCollectionKey,
            deviceRunning.getDeviceId());

    final String deviceCollectionKey = buildDeviceCollectionKey(deviceRunning.getDeviceId());
    StreamControlData dataFromDeviceCollection = streamControlDataHashOperations.get(deviceCollectionKey,
            deviceRunning.getUserId());

    if ( (null == dataFromUserCollection) || (null == dataFromDeviceCollection) ){
      // inconsistent data
      if (null != dataFromUserCollection) {
        streamControlDataHashOperations.delete(userCollectionKey,dataFromUserCollection.getDeviceId());
      }
      if (null != dataFromDeviceCollection) {
        streamControlDataHashOperations.delete(deviceCollectionKey,dataFromDeviceCollection.getUserId());
      }
      return false;
    } else if (!dataFromUserCollection.equals(dataFromDeviceCollection)) {
      //also inconsistent data
      streamControlDataHashOperations.delete(userCollectionKey,dataFromUserCollection.getDeviceId());
      streamControlDataHashOperations.delete(deviceCollectionKey,dataFromDeviceCollection.getUserId());
      return false;
    }

    //update data
    dataFromUserCollection.setStreamId(deviceRunning.getStreamId());
    dataFromUserCollection.setStatus(deviceRunning.getStatus());
    dataFromUserCollection.setTillTheEnd(deviceRunning.isTillTheEnd());

    //save data
    streamControlDataHashOperations.put(userCollectionKey, dataFromUserCollection.getDeviceId(),dataFromUserCollection);
    streamControlDataHashOperations.put(deviceCollectionKey, dataFromUserCollection.getUserId(),dataFromUserCollection);
    return true;
  }
}

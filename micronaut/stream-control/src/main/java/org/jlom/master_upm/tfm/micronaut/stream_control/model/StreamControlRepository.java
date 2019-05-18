package org.jlom.master_upm.tfm.micronaut.stream_control.model;



import com.fasterxml.jackson.core.JsonProcessingException;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.jlom.master_upm.tfm.micronaut.stream_control.model.api.IStreamControlRepository;
import org.jlom.master_upm.tfm.micronaut.stream_control.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.micronaut.stream_control.model.daos.StreamStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.jlom.master_upm.tfm.micronaut.stream_control.utils.JsonUtils.ObjectToJson;
import static org.jlom.master_upm.tfm.micronaut.stream_control.utils.JsonUtils.jsonToList;
import static org.jlom.master_upm.tfm.micronaut.stream_control.utils.JsonUtils.jsonToObject;


public class StreamControlRepository implements IStreamControlRepository {

  private static final Logger LOG = LoggerFactory.getLogger(StreamControlRepository.class);

  private static final String STREAMCONTROL_DATA_KEY = "StreamControlDataKey";
  private static final String USER_COLLECTION = "User";
  private static final String DEVICE_COLLECTION = "Dev";

  private StatefulRedisConnection<String, String> connection;

  public StreamControlRepository(StatefulRedisConnection<String, String> connection) {
    this.connection = connection;
  }

  private String buildKey(String collection, String pk, String ck) {
    return String.format("%s:%s:%s:%s",
            STREAMCONTROL_DATA_KEY,
            collection,
            pk,
            ck
    );
  }
  private String buildCollectionKey(String collection, long pk, long ck) {
    return buildKey(collection,
            String.valueOf(pk),
            String.valueOf(ck)
    );
  }

  private String buildCollectionKey(String collection, long pk) {
    return buildKey(collection,
            String.valueOf(pk),
            "*"
    );
  }
  private String buildUserCollectionKey(long userId) {
    return buildCollectionKey(USER_COLLECTION,userId);
  }

  private String buildUserCollectionKey(long userId, long deviceId) {
    return buildCollectionKey(USER_COLLECTION,userId,deviceId);
  }

  private String buildDeviceCollectionKey(long deviceId) {
    return buildCollectionKey(DEVICE_COLLECTION,deviceId);
  }

  private String buildDeviceCollectionKey(long deviceId, long userId) {
    return buildCollectionKey(DEVICE_COLLECTION,deviceId, userId);
  }

  @Override
  public StreamControlData findStreamingRunning(long userId, long deviceId) {
    LOG.info("StreamControlRepository::findStreamingRunning: userId:" + userId
            + " deviceId:" + deviceId
    );
    RedisCommands<String, String> redisApi = connection.sync();

    // use a different collection for each user
    final String userCollectionKey = buildUserCollectionKey(userId,deviceId);
    // and deviceId as key inside each collection.
    String jsonData = redisApi.get(userCollectionKey);
    try {
      StreamControlData streamControlData = jsonToObject(jsonData, StreamControlData.class);
      if ( (null == streamControlData) || (streamControlData.getStatus() == StreamStatus.DONE)) {
        return null;
      }
      return streamControlData;
    } catch (IOException e) {
      e.printStackTrace();
      LOG.error("StreamControlRepository::findStreamingRunning Problem while looking in database: " + e.getMessage());
      return null;
    }
  }

  @Override
  public StreamControlData isUserRunning(long userId) {
    LOG.info("StreamControlRepository::isUserRunning: userId:" + userId);
    RedisCommands<String, String> redisApi = connection.sync();

    final String userCollectionKey = buildUserCollectionKey(userId);

    String jsonData = redisApi.get(userCollectionKey);

    try {
      List<StreamControlData> values = jsonToList(jsonData, StreamControlData.class);
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

    } catch (IOException e) {
      e.printStackTrace();
      LOG.error("StreamControlRepository::isUserRunning Problem while looking in database: " + e.getMessage());
      return null;
    }
  }

  @Override
  public StreamControlData isDeviceRunning(long deviceId) {
    LOG.info("StreamControlRepository::isDeviceRunning: deviceId:" + deviceId);
    RedisCommands<String, String> redisApi = connection.sync();

    final String deviceCollectionKey = buildDeviceCollectionKey(deviceId);
    try {
      String jsonData = redisApi.get(deviceCollectionKey);
      List<StreamControlData> values = jsonToList(jsonData, StreamControlData.class);
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
    } catch (IOException e) {
    e.printStackTrace();
    LOG.error("StreamControlRepository::isDeviceRunning Problem while looking in database: " + e.getMessage());
    return null;
  }
  }

  @Override
  public void save(StreamControlData toInsert) {
    LOG.info("StreamControlRepository::save: toInsert:" + toInsert);
    RedisCommands<String, String> redisApi = connection.sync();

    String jsonData;
    try {
      jsonData = ObjectToJson(toInsert);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      LOG.error("Unable to convert to json: " + toInsert);
      return;
    }

    final String userCollectionKey = buildUserCollectionKey(toInsert.getUserId(),
            toInsert.getDeviceId());
    redisApi.set(userCollectionKey, jsonData);

    final String deviceCollectionKey = buildDeviceCollectionKey(toInsert.getDeviceId(),
            toInsert.getUserId());
    redisApi.set(deviceCollectionKey,jsonData);
  }

  @Override
  public boolean update(StreamControlData deviceRunning) {
    LOG.info("StreamControlRepository::update: toUpdate:" + deviceRunning);
    RedisCommands<String, String> redisApi = connection.sync();

    try {
      final String userCollectionKey = buildUserCollectionKey(deviceRunning.getUserId()
              , deviceRunning.getDeviceId()
      );
      String jsonData = redisApi.get(userCollectionKey);
      StreamControlData dataFromUserCollection = jsonToObject(jsonData, StreamControlData.class);


      final String deviceCollectionKey = buildDeviceCollectionKey(deviceRunning.getDeviceId(),
              deviceRunning.getUserId()
      );
      jsonData = redisApi.get(deviceCollectionKey);
      StreamControlData dataFromDeviceCollection = jsonToObject(jsonData, StreamControlData.class);

      if ((null == dataFromUserCollection) || (null == dataFromDeviceCollection)) {
        // inconsistent data
        if (null != dataFromUserCollection) {
          redisApi.del(userCollectionKey);
        }
        if (null != dataFromDeviceCollection) {
          redisApi.del(deviceCollectionKey);
        }
        return false;
      } else if (!dataFromUserCollection.equals(dataFromDeviceCollection)) {
        //also inconsistent data
        redisApi.del(userCollectionKey);
        redisApi.del(deviceCollectionKey);
        return false;
      }

      //update data
      dataFromUserCollection.setStreamId(deviceRunning.getStreamId());
      dataFromUserCollection.setStatus(deviceRunning.getStatus());
      dataFromUserCollection.setTillTheEnd(deviceRunning.isTillTheEnd());

      jsonData = ObjectToJson(dataFromUserCollection);
      //save data
      redisApi.set(userCollectionKey, jsonData);
      redisApi.set(deviceCollectionKey, jsonData);
      return true;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      LOG.error("StreamControlRepository::update: Json problem:" + e.getMessage());
      return false;
    } catch (IOException e) {
      LOG.error("StreamControlRepository::update exception:" + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }
}

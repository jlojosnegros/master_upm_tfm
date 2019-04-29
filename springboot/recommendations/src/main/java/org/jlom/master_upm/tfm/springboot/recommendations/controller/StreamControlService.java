package org.jlom.master_upm.tfm.springboot.recommendations.controller;


import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.StreamControlServiceCommands;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.StreamControlServiceQueries;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.StreamControlServiceResponse;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.StreamControlServiceResponseFailureException;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.StreamControlServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.StreamControlServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.StreamControlServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.StreamControlServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.in.StreamControlStreamingNotification;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.clients.InputUserDevice;
import org.jlom.master_upm.tfm.springboot.recommendations.model.api.IRecommendationsRepository;
import org.jlom.master_upm.tfm.springboot.recommendations.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.springboot.recommendations.model.daos.StreamStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class StreamControlService implements StreamControlServiceCommands, StreamControlServiceQueries {

  private final IRecommendationsRepository repository;
  private final RestTemplate restTemplate;
  private final NotificationsBusProducer notificationsBusProducer;

  private static final Logger LOG = LoggerFactory.getLogger(StreamControlService.class);

  public StreamControlService(IRecommendationsRepository repository, RestTemplate restTemplate, NotificationsBusProducer notificationsBusProducer) {
    this.repository = repository;
    this.restTemplate = restTemplate;
    this.notificationsBusProducer = notificationsBusProducer;
  }


  @Override
  public StreamControlServiceResponse play(long streamId, long deviceId) {
//
//    InputUserDevice userFromDevice;
//    try {
//      userFromDevice = restTemplate.getForObject("http://dynamic-service:8080/dynamic-data/user-device/device/" + deviceId
//              , InputUserDevice.class);
//    } catch (RestClientException ex) {
//      LOG.error("Exception while getting userId: " + ex.getMessage());
//      return new StreamControlServiceResponseFailureException("Exception while getting userId",ex);
//    }
//    if (null == userFromDevice) {
//      return new StreamControlServiceResponseFailureNotFound("Unable to find userId for deviceId",
//              "deviceId", deviceId);
//    }
//    long userId = Long.parseLong(userFromDevice.getUserId());
//
//    StreamControlData streaming = repository.findStreamingRunning(userId, deviceId);
//    if (null != streaming) {
//      return new StreamControlServiceResponseFailureInvalidInputParameter("Device id:" + deviceId +" is already playing",
//              "deviceId", deviceId);
//    }
//
//    //TODO Aqui es donde habria que interactuar con el player externo.
//
//    StreamControlData toInsert = StreamControlData.builder()
//            .userId(userId)
//            .deviceId(deviceId)
//            .streamId(streamId)
//            .status(StreamStatus.RUNNING)
//            .build();
//
//    repository.save(toInsert);
//
//    //publicar la notificacion.
//    notificationsBusProducer.publish(StreamControlStreamingNotification.builder()
//            .userId(String.valueOf(toInsert.getUserId()))
//            .deviceId(String.valueOf(toInsert.getDeviceId()))
//            .streamId(String.valueOf(toInsert.getStreamId()))
//            .operation(StreamControlStreamingNotification.Operation.PLAY)
//            .build());
//
//    StreamControlData streamingRunning = repository.findStreamingRunning(userId, deviceId);
//    if (null == streamingRunning) {
//      return new StreamControlServiceResponseFailureInternalError("Unable to save:" + toInsert);
//    }

    return new StreamControlServiceResponseOK(null);
  }



  @Override
  public StreamControlServiceResponse stop(long deviceId) {
    return changeStatus(deviceId,StreamStatus.DONE);
  }

  @Override
  public StreamControlServiceResponse pause(long deviceId) {
    return changeStatus(deviceId,StreamStatus.PAUSED);
  }

  private StreamControlServiceResponse changeStatus(long deviceId, StreamStatus status) {
    //find running stream in database
//    StreamControlData deviceRunning = repository.isDeviceRunning(deviceId);
//    if (null == deviceRunning) {
//      return new StreamControlServiceResponseFailureInvalidInputParameter("No running stream for deviceID",
//              "deviceId",
//              deviceId);
//    }
//
//    //TODO Aqui es donde habria que interactuar con el player externo.
//
//    notificationsBusProducer.publish(StreamControlStreamingNotification.builder()
//            .userId(String.valueOf(deviceRunning.getUserId()))
//            .deviceId(String.valueOf(deviceRunning.getDeviceId()))
//            .streamId(String.valueOf(deviceRunning.getStreamId()))
//            .operation((status == StreamStatus.DONE)?
//                    (StreamControlStreamingNotification.Operation.STOP):
//                    (StreamControlStreamingNotification.Operation.PAUSE))
//            .build());
//
//    //update data in database
//    deviceRunning.setStatus(status);
//    deviceRunning.setTillTheEnd(false);
//
//    if (! repository.update(deviceRunning)) {
//      return new StreamControlServiceResponseFailureInternalError("Unable to update " + deviceRunning);
//    }
    return new StreamControlServiceResponseOK(null);
  }
}

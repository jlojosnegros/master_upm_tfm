package org.jlom.master_upm.tfm.graalvm.stream_control.controller;


import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.StreamControlServiceCommands;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.StreamControlServiceQueries;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponse;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseFailureException;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseOK;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.out.OutBoundNotifications;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.out.StreamControlStreamingNotification;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.clients.InputUserDevice;
import org.jlom.master_upm.tfm.graalvm.stream_control.model.api.IStreamControlRepository;
import org.jlom.master_upm.tfm.graalvm.stream_control.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.graalvm.stream_control.model.daos.StreamStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

import static org.jlom.master_upm.tfm.graalvm.stream_control.utils.JsonUtils.jsonToObject;

@Singleton
public class StreamControlService implements StreamControlServiceCommands, StreamControlServiceQueries {

  private final IStreamControlRepository repository;

  private final OutBoundNotifications notificationsBusProducer;

  @Inject
  @Client("http://dynamic-service:8080")
  HttpClient httpClient;

  private static final Logger LOG = LoggerFactory.getLogger(StreamControlService.class);

  public StreamControlService(IStreamControlRepository repository, OutBoundNotifications notificationsBusProducer) {
    this.repository = repository;
    this.notificationsBusProducer = notificationsBusProducer;
  }


  @Override
  public StreamControlServiceResponse play(long streamId, long deviceId) {

    InputUserDevice userFromDevice;
    try {
      String body = httpClient.toBlocking().retrieve(HttpRequest.GET("/dynamic-data/user-device/device/" + deviceId));
      userFromDevice = jsonToObject(body, InputUserDevice.class);
    } catch (IOException e) {
      e.printStackTrace();
      LOG.error("Exception while getting userId: " + e.getMessage());
      return new StreamControlServiceResponseFailureException("Exception while getting userId",e);
    }
    if (null == userFromDevice) {
      return new StreamControlServiceResponseFailureNotFound("Unable to find userId for deviceId",
              "deviceId", deviceId);
    }
    long userId = Long.parseLong(userFromDevice.getUserId());

    StreamControlData streaming = repository.findStreamingRunning(userId, deviceId);
    if (null != streaming) {
      return new StreamControlServiceResponseFailureInvalidInputParameter("Device id:" + deviceId +" is already playing",
              "deviceId", deviceId);
    }

    //TODO Aqui es donde habria que interactuar con el player externo.

    StreamControlData toInsert = StreamControlData.builder()
            .userId(userId)
            .deviceId(deviceId)
            .streamId(streamId)
            .status(StreamStatus.RUNNING)
            .build();

    repository.save(toInsert);

    //publicar la notificacion.
    notificationsBusProducer.streamingNotifications(StreamControlStreamingNotification.builder()
            .userId(String.valueOf(toInsert.getUserId()))
            .deviceId(String.valueOf(toInsert.getDeviceId()))
            .streamId(String.valueOf(toInsert.getStreamId()))
            .operation(StreamControlStreamingNotification.Operation.PLAY)
            .build());

    StreamControlData streamingRunning = repository.findStreamingRunning(userId, deviceId);
    if (null == streamingRunning) {
      return new StreamControlServiceResponseFailureInternalError("Unable to save:" + toInsert);
    }

    return new StreamControlServiceResponseOK(streamingRunning);
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
    StreamControlData deviceRunning = repository.isDeviceRunning(deviceId);
    if (null == deviceRunning) {
      return new StreamControlServiceResponseFailureInvalidInputParameter("No running stream for deviceID",
              "deviceId",
              deviceId);
    }

    //TODO Aqui es donde habria que interactuar con el player externo.

    notificationsBusProducer.streamingNotifications(StreamControlStreamingNotification.builder()
            .userId(String.valueOf(deviceRunning.getUserId()))
            .deviceId(String.valueOf(deviceRunning.getDeviceId()))
            .streamId(String.valueOf(deviceRunning.getStreamId()))
            .operation((status == StreamStatus.DONE)?
                    (StreamControlStreamingNotification.Operation.STOP):
                    (StreamControlStreamingNotification.Operation.PAUSE))
            .build());

    //update data in database
    deviceRunning.setStatus(status);
    deviceRunning.setTillTheEnd(false);

    if (! repository.update(deviceRunning)) {
      return new StreamControlServiceResponseFailureInternalError("Unable to update " + deviceRunning);
    }
    return new StreamControlServiceResponseOK(deviceRunning);
  }
}

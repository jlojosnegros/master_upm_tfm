package org.jlom.master_upm.tfm.springboot.stream_control.controller;


import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.StreamControlServiceCommands;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.StreamControlServiceQueries;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponse;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseFailureException;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.clients.InputUserDevice;
import org.jlom.master_upm.tfm.springboot.stream_control.model.api.IStreamControlRepository;
import org.jlom.master_upm.tfm.springboot.stream_control.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.springboot.stream_control.model.daos.StreamStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class StreamControlService implements StreamControlServiceCommands, StreamControlServiceQueries {

  private final IStreamControlRepository repository;
  private final RestTemplate restTemplate;

  private static final Logger LOG = LoggerFactory.getLogger(StreamControlService.class);

  public StreamControlService(IStreamControlRepository repository, RestTemplate restTemplate) {
    this.repository = repository;
    this.restTemplate = restTemplate;
  }


  @Override
  public StreamControlServiceResponse play(long streamId, long deviceId) {

    InputUserDevice userFromDevice;
    try {
      userFromDevice = restTemplate.getForObject("http://dynamic-service/dynamic-data/user-device/device/" + deviceId
              , InputUserDevice.class);
    } catch (RestClientException ex) {
      LOG.error("Exception while getting userId: " + ex.getMessage());
      return new StreamControlServiceResponseFailureException("Exception while getting userId",ex);
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

    //jlom TODO publicar la notificacion.

    StreamControlData streamingRunning = repository.findStreamingRunning(userId, deviceId);
    if (null == streamingRunning) {
      return new StreamControlServiceResponseFailureInternalError("Unable to save:" + toInsert);
    }

    return new StreamControlServiceResponseOK(streamingRunning);
  }



  @Override
  public StreamControlServiceResponse stop(long deviceId) {
    return null;
  }

  @Override
  public StreamControlServiceResponse pause(long deviceId) {
    return null;
  }
}

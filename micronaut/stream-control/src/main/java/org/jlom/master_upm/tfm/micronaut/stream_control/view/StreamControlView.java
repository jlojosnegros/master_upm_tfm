package org.jlom.master_upm.tfm.micronaut.stream_control.view;


import io.micronaut.configuration.rabbitmq.annotation.Queue;
import io.micronaut.configuration.rabbitmq.annotation.RabbitListener;
import org.jlom.master_upm.tfm.micronaut.stream_control.config.RabbitConfiguration;
import org.jlom.master_upm.tfm.micronaut.stream_control.controller.StreamControlService;
import org.jlom.master_upm.tfm.micronaut.stream_control.controller.api.dtos.StreamControlServiceResponse;
import org.jlom.master_upm.tfm.micronaut.stream_control.view.api.StreamControlInterface;
import org.jlom.master_upm.tfm.micronaut.stream_control.view.api.dtos.StreamControlReturnValue;
import org.jlom.master_upm.tfm.micronaut.stream_control.view.response_handlers.StreamControlServiceResponseHandlerRPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@RabbitListener
public class StreamControlView implements StreamControlInterface {

  private static final Logger LOG = LoggerFactory.getLogger(StreamControlView.class);

  @Inject
  private StreamControlService service;

  @Queue(RabbitConfiguration.RPC_PLAY_QUEUE)
  @Override
  public StreamControlReturnValue play(long streamId, long deviceId) {
    StreamControlServiceResponse response = service.play(streamId, deviceId);

    return response.accept(new StreamControlServiceResponseHandlerRPC());
  }

  @Queue(RabbitConfiguration.RPC_STOP_QUEUE)
  @Override
  public StreamControlReturnValue stop(long deviceId) {

    StreamControlServiceResponse response = service.stop(deviceId);

    return response.accept(new StreamControlServiceResponseHandlerRPC());
  }

  @Queue(RabbitConfiguration.RPC_PAUSE_QUEUE)
  @Override
  public StreamControlReturnValue pause(long deviceId) {
    StreamControlServiceResponse response = service.stop(deviceId);

    return response.accept(new StreamControlServiceResponseHandlerRPC());
  }
}

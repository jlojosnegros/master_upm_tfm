package org.jlom.master_upm.tfm.springboot.user_categories.view;

import org.jlom.master_upm.tfm.springboot.user_categories.controller.StreamControlService;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.StreamControlServiceResponse;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.StreamControlInterface;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.StreamControlReturnValue;
import org.jlom.master_upm.tfm.springboot.user_categories.view.response_handlers.StreamControlServiceResponseHandlerRPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class StreamControlView implements StreamControlInterface {

  private static final Logger LOG = LoggerFactory.getLogger(StreamControlView.class);

  @Autowired
  private StreamControlService service;

  @Override
  public StreamControlReturnValue play(long streamId, long deviceId) {
    StreamControlServiceResponse response = service.play(streamId, deviceId);

    return response.accept(new StreamControlServiceResponseHandlerRPC());
  }

  @Override
  public StreamControlReturnValue stop(long deviceId) {

    StreamControlServiceResponse response = service.stop(deviceId);

    return response.accept(new StreamControlServiceResponseHandlerRPC());
  }

  @Override
  public StreamControlReturnValue pause(long deviceId) {
    StreamControlServiceResponse response = service.stop(deviceId);

    return response.accept(new StreamControlServiceResponseHandlerRPC());
  }
}

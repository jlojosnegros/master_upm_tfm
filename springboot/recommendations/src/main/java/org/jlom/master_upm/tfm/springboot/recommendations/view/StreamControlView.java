package org.jlom.master_upm.tfm.springboot.recommendations.view;

import org.jlom.master_upm.tfm.springboot.recommendations.controller.RecommendationsService;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.RecommendationsServiceResponse;
import org.jlom.master_upm.tfm.springboot.recommendations.view.api.StreamControlInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class StreamControlView implements StreamControlInterface {

  private static final Logger LOG = LoggerFactory.getLogger(StreamControlView.class);

  @Autowired
  private RecommendationsService service;

  @Override
  public StreamControlReturnValue play(long streamId, long deviceId) {
    RecommendationsServiceResponse response = service.play(streamId, deviceId);

    return response.accept(new StreamControlServiceResponseHandlerRPC());
  }

  @Override
  public StreamControlReturnValue stop(long deviceId) {

    RecommendationsServiceResponse response = service.stop(deviceId);

    return response.accept(new StreamControlServiceResponseHandlerRPC());
  }

  @Override
  public StreamControlReturnValue pause(long deviceId) {
    RecommendationsServiceResponse response = service.stop(deviceId);

    return response.accept(new StreamControlServiceResponseHandlerRPC());
  }
}

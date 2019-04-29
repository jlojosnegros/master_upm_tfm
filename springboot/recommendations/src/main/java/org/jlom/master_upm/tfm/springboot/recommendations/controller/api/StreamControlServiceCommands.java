package org.jlom.master_upm.tfm.springboot.recommendations.controller.api;


import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.StreamControlServiceResponse;

public interface StreamControlServiceCommands {
  StreamControlServiceResponse play(final long streamId, final long deviceId);
  StreamControlServiceResponse stop(final long deviceId);
  StreamControlServiceResponse pause(final long deviceId);
}

package org.jlom.master_upm.tfm.graalvm.stream_control.controller.api;


import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponse;

public interface StreamControlServiceCommands {
  StreamControlServiceResponse play(final long streamId, final long deviceId);
  StreamControlServiceResponse stop(final long deviceId);
  StreamControlServiceResponse pause(final long deviceId);
}

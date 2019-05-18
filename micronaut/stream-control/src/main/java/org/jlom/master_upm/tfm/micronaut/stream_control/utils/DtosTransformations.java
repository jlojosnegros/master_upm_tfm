package org.jlom.master_upm.tfm.micronaut.stream_control.utils;


import org.jlom.master_upm.tfm.micronaut.stream_control.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.micronaut.stream_control.view.api.dtos.InputStreamData;


public class DtosTransformations {

  public static StreamControlData viewToService(final InputStreamData inputStreamData) {
    return StreamControlData.builder()
            .userId(Long.parseLong(inputStreamData.getUserId()))
            .deviceId(Long.parseLong(inputStreamData.getDeviceId()))
            .streamId(Long.parseLong(inputStreamData.getStreamId()))
            .build();
  }

  public static InputStreamData serviceToView(final StreamControlData streamControlData) {
    return InputStreamData.builder()
            .userId(String.valueOf(streamControlData.getUserId()))
            .deviceId(String.valueOf(streamControlData.getDeviceId()))
            .streamId(String.valueOf(streamControlData.getStreamId()))
            .build();
  }
}
package org.jlom.master_upm.tfm.springboot.user_categories.utils;


import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputUserCategoryData;

public class DtosTransformations {

  public static StreamControlData viewToService(final InputUserCategoryData inputUserCategoryData) {
    return StreamControlData.builder()
            .userId(Long.parseLong(inputUserCategoryData.getUserId()))
            .deviceId(Long.parseLong(inputUserCategoryData.getDeviceId()))
            .streamId(Long.parseLong(inputUserCategoryData.getStreamId()))
            .build();
  }

  public static InputUserCategoryData serviceToView(final StreamControlData streamControlData) {
    return InputUserCategoryData.builder()
            .userId(String.valueOf(streamControlData.getUserId()))
            .deviceId(String.valueOf(streamControlData.getDeviceId()))
            .streamId(String.valueOf(streamControlData.getStreamId()))
            .build();
  }
}
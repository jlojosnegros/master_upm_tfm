package org.jlom.master_upm.tfm.graalvm.stream_control.view;

import lombok.Getter;
import lombok.Setter;

public class StartTime {

  private StartTime(){}

  private static StartTime instance = new StartTime();
  public static StartTime getInstance() {

    return instance;
  }

  @Getter
  @Setter
  long startTime;


}

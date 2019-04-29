package org.jlom.master_upm.tfm.springboot.recommendations.controller.api.in;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;


public interface InBoundNotifications {
  String StreamControlNotifications = "stream-control-notifications";

  @Input(StreamControlNotifications)
  SubscribableChannel streamingNotifications();
}

package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.out;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;


public interface OutBoundNotifications {
  String StreamControlNotifications = "stream-control-notifications";

  @Output(StreamControlNotifications)
  MessageChannel streamingNotifications();
}

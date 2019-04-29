package org.jlom.master_upm.tfm.springboot.recommendations.controller;

import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.in.InBoundNotifications;
import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.in.StreamControlStreamingNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;

@Configuration
@EnableBinding(InBoundNotifications.class)
public class NotificationsBusProducer {

  private static final Logger LOG = LoggerFactory.getLogger(NotificationsBusProducer.class);

  private final MessageChannel output;


  public NotificationsBusProducer(@Qualifier(InBoundNotifications.StreamControlNotifications) final MessageChannel output) {
    this.output = output;
  }

  @SendTo(InBoundNotifications.StreamControlNotifications)
  public void publish(StreamControlStreamingNotification notification) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Publishing Stream notification notification {} to message bus", notification.toString());
    }
    output.send(MessageBuilder.withPayload(notification).build());
  }
}

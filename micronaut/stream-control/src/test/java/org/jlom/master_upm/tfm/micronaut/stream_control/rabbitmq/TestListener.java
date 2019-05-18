package org.jlom.master_upm.tfm.micronaut.stream_control.rabbitmq;

import io.micronaut.configuration.rabbitmq.annotation.Queue;
import io.micronaut.configuration.rabbitmq.annotation.RabbitListener;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jlom.master_upm.tfm.micronaut.stream_control.config.RabbitConfiguration;
import org.jlom.master_upm.tfm.micronaut.stream_control.controller.api.out.StreamControlStreamingNotification;

import java.util.List;

@RabbitListener
@Requires(env = Environment.TEST)
@Getter
@Slf4j
public class TestListener {

  List<StreamControlStreamingNotification> notifications;

  @Queue(RabbitConfiguration.PUBLISH_QUEUE)
  public void notification(StreamControlStreamingNotification notification) {
    log.info("Incoming notification: " + notification);
    notifications.add(notification);
  }

  public void cleanNotifications() {
    notifications.clear();
  }
}


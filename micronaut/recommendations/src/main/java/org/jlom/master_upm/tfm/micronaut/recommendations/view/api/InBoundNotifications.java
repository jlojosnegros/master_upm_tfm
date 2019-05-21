package org.jlom.master_upm.tfm.micronaut.recommendations.view.api;

import io.micronaut.configuration.rabbitmq.annotation.Queue;
import io.micronaut.configuration.rabbitmq.annotation.RabbitListener;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import org.jlom.master_upm.tfm.micronaut.recommendations.config.RabbitConfiguration;
import org.jlom.master_upm.tfm.micronaut.recommendations.view.RecommendationsIncomingNotificationHandler;
import org.jlom.master_upm.tfm.micronaut.recommendations.view.api.dtos.StreamControlStreamingNotification;

@RabbitListener
//@Requires(notEnv = Environment.TEST)
public class InBoundNotifications {

  private RecommendationsIncomingNotificationHandler handler;

  public InBoundNotifications(final RecommendationsIncomingNotificationHandler handler) {
    this.handler = handler;
  }

  @Queue(RabbitConfiguration.PUBLISH_QUEUE)
  public void streamingNotifications(StreamControlStreamingNotification notification) {
    handler.incomingStreamControlNotifications(notification);
  }
}

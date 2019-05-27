package org.jlom.master_upm.tfm.graalvm.recommendations.view.api;

import io.micronaut.configuration.rabbitmq.annotation.Queue;
import io.micronaut.configuration.rabbitmq.annotation.RabbitListener;
import org.jlom.master_upm.tfm.graalvm.recommendations.config.RabbitConfiguration;
import org.jlom.master_upm.tfm.graalvm.recommendations.view.RecommendationsIncomingNotificationHandler;
import org.jlom.master_upm.tfm.graalvm.recommendations.view.api.dtos.StreamControlStreamingNotification;

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

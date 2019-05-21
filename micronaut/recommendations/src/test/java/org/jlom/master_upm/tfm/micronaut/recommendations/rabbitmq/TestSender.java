package org.jlom.master_upm.tfm.micronaut.recommendations.rabbitmq;

import io.micronaut.configuration.rabbitmq.annotation.Binding;
import io.micronaut.configuration.rabbitmq.annotation.RabbitClient;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import org.jlom.master_upm.tfm.micronaut.recommendations.config.RabbitConfiguration;
import org.jlom.master_upm.tfm.micronaut.recommendations.view.api.dtos.StreamControlStreamingNotification;

@RabbitClient(RabbitConfiguration.EXCHANGE)
@Requires(env = Environment.TEST)
public interface TestSender {

  @Binding(RabbitConfiguration.PUBLISH_ROUTING_KEY)
  void sendNotification(final StreamControlStreamingNotification notification);
}

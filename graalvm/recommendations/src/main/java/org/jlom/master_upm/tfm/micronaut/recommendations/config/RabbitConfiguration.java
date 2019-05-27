package org.jlom.master_upm.tfm.micronaut.recommendations.config;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import io.micronaut.configuration.rabbitmq.connect.ChannelInitializer;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.Arrays;

@Singleton
@Requires(notEnv = Environment.TEST)
@Slf4j
public class RabbitConfiguration extends ChannelInitializer {

  public static final String EXCHANGE = "micronaut";

  public static final String PUBLISH_QUEUE = "stream-control-notifications";
  public static final String PUBLISH_ROUTING_KEY = "stream-control.notifications";



  @Override
  public void initialize(Channel channel) throws IOException {

    try {
      // Declare exchange for all the messages
      channel.exchangeDeclare(EXCHANGE,
              BuiltinExchangeType.DIRECT,
              true
      );

      // Configuration for Publish Notifications
      channel.queueDeclare(PUBLISH_QUEUE,
              true,
              false,
              false,
              null);

      channel.queueBind(PUBLISH_QUEUE,
              EXCHANGE,
              PUBLISH_ROUTING_KEY
      );

    } catch (IOException ex) {
      log.error("JLOM: Exception while configuring rabbitmq: " + ex.getMessage());
      log.error("JLOM: Exception while configuring rabbitmq: " + Arrays.toString(ex.getStackTrace()));
    }
  }
}


package org.jlom.master_upm.tfm.graalvm.stream_control.config;


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

  public static final String RPC_PLAY_QUEUE = "stream-control-rpc-play";
  public static final String RPC_PLAY_ROUTING_KEY = "stream-control.rpc.play";

  public static final String RPC_STOP_QUEUE = "stream-control-rpc-stop";
  public static final String RPC_STOP_ROUTING_KEY = "stream-control.rpc.stop";

  public static final String RPC_PAUSE_QUEUE = "stream-control-rpc-pause";
  public static final String RPC_PAUSE_ROUTING_KEY = "stream-control.rpc.pause";

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


      // Configuration for RPC
      channel.queueDeclare(RPC_PLAY_QUEUE,
              true,
              false,
              false,
              null);
      channel.queueBind(RPC_PLAY_QUEUE,
              EXCHANGE,
              RPC_PLAY_ROUTING_KEY
      );

      channel.queueDeclare(RPC_STOP_QUEUE,
              true,
              false,
              false,
              null);
      channel.queueBind(RPC_STOP_QUEUE,
              EXCHANGE,
              RPC_STOP_ROUTING_KEY
      );

      channel.queueDeclare(RPC_PAUSE_QUEUE,
              true,
              false,
              false,
              null);
      channel.queueBind(RPC_PAUSE_QUEUE,
              EXCHANGE,
              RPC_PAUSE_ROUTING_KEY
      );
    } catch (IOException ex) {
      log.error("JLOM: Exception while configuring rabbitmq: " + ex.getMessage());
      log.error("JLOM: Exception while configuring rabbitmq: " + Arrays.toString(ex.getStackTrace()));
    }
  }
}

package org.jlom.master_upm.tfm.micronaut.recommendations.utils;

import io.lettuce.core.RedisURI;
import io.micronaut.configuration.lettuce.AbstractRedisConfiguration;
import io.micronaut.configuration.lettuce.RedisSetting;
import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.io.socket.SocketUtils;
import io.micronaut.core.util.StringUtils;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;


@Requires(classes = RedisServer.class)
@Requires(beans = AbstractRedisConfiguration.class)
@Replaces(io.micronaut.configuration.lettuce.test.EmbeddedRedisServer.class)
@Requires(env = Environment.TEST)
@Factory
public class EmbeddedRedisServer implements BeanCreatedEventListener<AbstractRedisConfiguration>, Closeable {

  private static final String DEFAULT_MAXMEMORY_SETTING = "maxmemory 256M";
  private final Configuration embeddedConfiguration;
  private RedisServer redisServer;

  /**
   * Constructor.
   *
   * @param embeddedConfiguration embeddedConfiguration
   */
  public EmbeddedRedisServer(Configuration embeddedConfiguration) {
    this.embeddedConfiguration = embeddedConfiguration;
  }

  @Override
  public AbstractRedisConfiguration onCreated(BeanCreatedEvent<AbstractRedisConfiguration> event) {
    AbstractRedisConfiguration configuration = event.getBean();
    Optional<RedisURI> uri = configuration.getUri();
    int port = configuration.getPort();
    String host = configuration.getHost();
    if (uri.isPresent()) {
      RedisURI redisURI = uri.get();
      port = redisURI.getPort();
      host = redisURI.getHost();

    }
    if (StringUtils.isNotEmpty(host) && host.equals("localhost") && SocketUtils.isTcpPortAvailable(port)) {
      RedisServerBuilder builder = embeddedConfiguration.builder;
      builder.port(port);
      builder.setting(DEFAULT_MAXMEMORY_SETTING);
      redisServer = builder.build();
      redisServer.start();

    }
    return configuration;
  }

  @Override
  @PreDestroy
  public void close() throws IOException {
    if (redisServer != null) {
      redisServer.stop();
    }
  }

  public void start() {
    if (redisServer != null) {
      if (!redisServer.isActive()) {
        redisServer.start();
      }
    }
  }

  public void stop() {
    if (redisServer != null) {
      redisServer.stop();
    }
  }

  /**
   * Configuration properties for embedded Redis.
   */
  @ConfigurationProperties(RedisSetting.REDIS_EMBEDDED)
  @Requires(classes = RedisServerBuilder.class)
  public static class Configuration {
    @ConfigurationBuilder(
            prefixes = ""
    )
    RedisServerBuilder builder = new RedisServerBuilder().port(SocketUtils.findAvailableTcpPort());
  }
}

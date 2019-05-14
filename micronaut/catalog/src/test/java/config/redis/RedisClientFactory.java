package config.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.micronaut.configuration.lettuce.AbstractRedisClientFactory;
import io.micronaut.configuration.lettuce.AbstractRedisConfiguration;
import io.micronaut.configuration.lettuce.DefaultRedisClientFactory;
import io.micronaut.configuration.lettuce.DefaultRedisConfiguration;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;

import javax.inject.Singleton;

@Requires(beans = DefaultRedisConfiguration.class)
@Requires(env = "test")
@Primary
@Singleton
@Factory
@Replaces(bean = DefaultRedisClientFactory.class)
public class RedisClientFactory extends AbstractRedisClientFactory {

  @Bean(preDestroy = "shutdown")
  @Singleton
  @Primary
  @Override
  public RedisClient redisClient(@Primary AbstractRedisConfiguration config) {
    return super.redisClient(config);
  }


  @Bean(preDestroy = "close")
  @Singleton
  @Primary
  public StatefulRedisConnection<String, Object> myRedisConnection(@Primary RedisClient redisClient) {
    return redisClient.connect(new SerializedObjectCodec());
  }

  @Bean(preDestroy = "close")
  @Singleton
  @Primary
  @Override
  public StatefulRedisConnection<String, String> redisConnection(@Primary RedisClient redisClient) {
    return redisClient.connect();
  }


  @Override
  @Bean(preDestroy = "close")
  @Singleton
  public StatefulRedisPubSubConnection<String, String> redisPubSubConnection(@Primary RedisClient redisClient) {
    return super.redisPubSubConnection(redisClient);
  }
}

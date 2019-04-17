package org.jlom.master_upm.tfm.springboot.catalog.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

  @Bean
  RedisStandaloneConfiguration redisStandaloneConfiguration() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("catalog-db");
    redisStandaloneConfiguration.setPassword(RedisPassword.of("catalog-passwd"));
    return redisStandaloneConfiguration;
  }

  @Bean
  public JedisConnectionFactory jedisConnectionFactory(RedisStandaloneConfiguration configuration) {
    return new JedisConnectionFactory(configuration);
  }


  @Bean
  public RedisTemplate<String,Object> redisTemplate(JedisConnectionFactory factory) {

    final RedisTemplate<String,Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return template;
  }
}

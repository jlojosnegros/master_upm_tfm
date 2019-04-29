package org.jlom.master_upm.tfm.springboot.recommendations.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

  @Bean
  public RedisStandaloneConfiguration redisStandaloneConfiguration() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    
    redisStandaloneConfiguration.setHostName("recommendations-db");
    redisStandaloneConfiguration.setPort(6379);
    
    redisStandaloneConfiguration.setPassword(RedisPassword.of("recommendations-passwd"));
    return redisStandaloneConfiguration;
  }

  @Bean
  public LettuceConnectionFactory jedisConnectionFactory(RedisStandaloneConfiguration configuration) {
    return new LettuceConnectionFactory(configuration);

  }


  @Bean
  @ConditionalOnMissingBean(name = "redisTemplate")
  @Primary
  public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory) {

    final RedisTemplate<String,Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return template;
  }
}

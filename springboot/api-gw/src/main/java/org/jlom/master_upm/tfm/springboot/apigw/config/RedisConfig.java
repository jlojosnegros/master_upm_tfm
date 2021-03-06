package org.jlom.master_upm.tfm.springboot.apigw.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

  @Bean
  public RedisStandaloneConfiguration redisStandaloneConfiguration() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(); //("apigw-db", 6379);
    redisStandaloneConfiguration.setHostName("localhost");
    redisStandaloneConfiguration.setPort(6379);
    //redisStandaloneConfiguration.setPassword(RedisPassword.of("apigw-passwd"));
    redisStandaloneConfiguration.setDatabase(1);
    return redisStandaloneConfiguration;
  }

  @Bean
  public LettuceConnectionFactory connectionFactory(RedisStandaloneConfiguration configuration) {
    return new LettuceConnectionFactory(configuration);

  }


  @Bean
  public RedisTemplate<String,Object> redisTemplate(LettuceConnectionFactory factory) {

    final RedisTemplate<String,Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return template;
  }
}

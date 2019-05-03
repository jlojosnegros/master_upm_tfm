package org.jlom.master_upm.tfm.springboot.apigw.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
@Profile("test")
@EnableRedisRepositories
public class RedisTestConfiguration {

  @Bean
  @Profile("test")
  @Primary
  public RedisStandaloneConfiguration redisStandaloneConfigurationTest() {
    var redisContainerIpAddress = "localhost";
    var redisFirstMappedPort = 6379;
    //LOG.info("jlom: -=* TestConfiguration Redis Container running on: " + redisContainerIpAddress + ":" + redisFirstMappedPort);
    var configuration = new RedisStandaloneConfiguration();
    configuration.setHostName(redisContainerIpAddress);
    configuration.setPort(redisFirstMappedPort);
    return configuration;
  }

  @Bean
  @Profile("test")
  @Primary
  public RedisTemplate<byte[],byte[]> redisTemplateTest(RedisConnectionFactory factory) {

    final RedisTemplate<byte[],byte[]> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return template;
  }
}



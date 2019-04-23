package org.jlom.master_upm.tfm.springboot.stream_control.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;

@Configuration
@Profile("test")
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
}


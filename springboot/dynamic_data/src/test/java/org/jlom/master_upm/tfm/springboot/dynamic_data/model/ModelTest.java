package org.jlom.master_upm.tfm.springboot.dynamic_data.model;


import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.api.IDevicesRepository;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.api.IUserDevicesRepository;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos.Device;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos.UserDevice;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class ModelTest {

  @ClassRule
  public static GenericContainer redis = new GenericContainer<>("redis:5.0.4-alpine")
          .withExposedPorts(6379);

  @Autowired
  private IUserDevicesRepository userDevicesRepository;

  @Autowired
  private IDevicesRepository devicesRepository;


  private static final Logger LOG = LoggerFactory.getLogger(ModelTest.class);

  @Before
  public void setup() {
    String redisContainerIpAddress = redis.getContainerIpAddress();
    Integer redisFirstMappedPort = redis.getFirstMappedPort();
    LOG.info("-=* Redis Container running on: " + redisContainerIpAddress + ":" + redisFirstMappedPort);
  }

  @TestConfiguration
  static public class RedisTestConfiguration {

    @Bean
    @Profile("test")
    @Primary
    public RedisStandaloneConfiguration redisStandaloneConfigurationTest() {
      var redisContainerIpAddress = redis.getContainerIpAddress();
      var redisFirstMappedPort = redis.getFirstMappedPort();
      LOG.info("jlom: -=* TestConfiguration Redis Container running on: " + redisContainerIpAddress + ":" + redisFirstMappedPort);
      var configuration = new RedisStandaloneConfiguration();
      configuration.setHostName(redisContainerIpAddress);
      configuration.setPort(redisFirstMappedPort);
      return configuration;
    }
  }


  @Test
  public void dummy() {
    UserDevice built = UserDevice.builder().userId(1).build();
    userDevicesRepository.add(built);


    UserDevice byUserId = userDevicesRepository.findByUserId(built.getUserId());

    Assertions.assertThat(byUserId).isNotNull();
    Assertions.assertThat(byUserId).isEqualTo(built);


  }
}

package org.jlom.master_upm.tfm.springboot.dynamic_data.model;


import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.api.IUserDevicesRepository;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos.UserDevice;
import org.junit.After;
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

import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class ModelTest {

  @ClassRule
  public static GenericContainer redis = new GenericContainer<>("redis:5.0.4-alpine")
          .withExposedPorts(6379);

  //private GenericContainer redis = new GenericContainer<>("redis:5.0.4-alpine")
  @Autowired
  private IUserDevicesRepository userDevicesRepository;

//  @Autowired
//  private IDevicesRepository devicesRepository;


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
  public void given_ANonExistingEntity_when_adding_then_ShouldWorkFine() {

    UserDevice built = UserDevice.builder()
            .userId(1)
            .devices(Set.of(2L))
            .build();
    userDevicesRepository.add(built);


    UserDevice byUserId = userDevicesRepository.findByUserId(built.getUserId());

    Assertions.assertThat(byUserId).isNotNull();
    Assertions.assertThat(byUserId).isEqualTo(built);
  }

  @Test
  public void given_AnExistingEntity_when_adding_then_Should_NOT_Work() {

    UserDevice built = UserDevice.builder()
            .userId(1)
            .devices(Set.of(2L))
            .build();

    boolean added = userDevicesRepository.add(built);
    Assertions.assertThat(added).isTrue();

    UserDevice builtAgain = UserDevice.builder()
            .userId(1)
            .devices(Set.of(22L))
            .build();

    boolean addedAgain = userDevicesRepository.add(builtAgain);
    Assertions.assertThat(addedAgain).isFalse();

    UserDevice byUserId = userDevicesRepository.findByUserId(built.getUserId());

    Assertions.assertThat(byUserId).isNotNull();
    Assertions.assertThat(byUserId).isEqualTo(built);
    Assertions.assertThat(byUserId).isNotEqualTo(builtAgain);
  }

  @Test
  public void given_AnExistingEntity_when_updating_then_ShouldWorkFine() {

    UserDevice built = UserDevice.builder()
            .userId(1)
            .devices(Set.of(2L))
            .build();

    boolean added = userDevicesRepository.add(built);
    Assertions.assertThat(added).isTrue();

    UserDevice builtAgain = UserDevice.builder()
            .userId(1)
            .devices(Set.of(22L))
            .build();

    Assertions.assertThat(userDevicesRepository.update(builtAgain)).isTrue();


    UserDevice byUserId = userDevicesRepository.findByUserId(built.getUserId());

    Assertions.assertThat(byUserId).isNotNull();
    Assertions.assertThat(byUserId).isEqualTo(builtAgain);
    Assertions.assertThat(byUserId).isNotEqualTo(built);
  }

  @Test
  public void given_ANonExistingEntity_when_updating_then_Should_NOT_Work() {

    UserDevice built = UserDevice.builder()
            .userId(1)
            .devices(Set.of(2L))
            .build();

    boolean added = userDevicesRepository.add(built);
    Assertions.assertThat(added).isTrue();

    UserDevice builtAgain = UserDevice.builder()
            .userId(2)
            .devices(Set.of(22L))
            .build();

    Assertions.assertThat(userDevicesRepository.update(builtAgain)).isFalse();


    UserDevice byUserId = userDevicesRepository.findByUserId(built.getUserId());

    Assertions.assertThat(byUserId).isNotNull();
    Assertions.assertThat(byUserId).isEqualTo(built);
    Assertions.assertThat(byUserId).isNotEqualTo(builtAgain);

    UserDevice byUserIdAgain = userDevicesRepository.findByUserId(builtAgain.getUserId());
    Assertions.assertThat(byUserIdAgain).isNull();

  }

  @Test
  public void given_AnExistingEntity_when_deleting_then_ShouldWorkFine() {

    UserDevice built = UserDevice.builder()
            .userId(1)
            .devices(Set.of(2L))
            .build();

    boolean added = userDevicesRepository.add(built);
    Assertions.assertThat(added).isTrue();


    Long delete = userDevicesRepository.delete(built.getUserId());
    Assertions.assertThat(delete).isEqualTo(1);

    UserDevice byUserId = userDevicesRepository.findByUserId(built.getUserId());

    Assertions.assertThat(byUserId).isNull();
  }

  @Test
  public void given_ANonExistingEntity_when_deleting_then_Should_NOT_WorkFine() {

    UserDevice built = UserDevice.builder()
            .userId(1)
            .devices(Set.of(2L))
            .build();

    boolean added = userDevicesRepository.add(built);
    Assertions.assertThat(added).isTrue();

    UserDevice builtAgain = UserDevice.builder()
            .userId(2)
            .devices(Set.of(22L))
            .build();

    Long delete = userDevicesRepository.delete(builtAgain.getUserId());
    Assertions.assertThat(delete).isEqualTo(0);

    UserDevice byUserId = userDevicesRepository.findByUserId(builtAgain.getUserId());
    Assertions.assertThat(byUserId).isNull();
  }
}

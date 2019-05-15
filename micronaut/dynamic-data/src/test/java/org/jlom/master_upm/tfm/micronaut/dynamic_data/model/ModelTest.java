package org.jlom.master_upm.tfm.micronaut.dynamic_data.model;

import io.micronaut.context.ApplicationContext;
import io.micronaut.test.annotation.MicronautTest;
import org.jlom.master_upm.tfm.micronaut.dynamic_data.model.api.IUserDevicesRepository;
import org.jlom.master_upm.tfm.micronaut.dynamic_data.model.daos.UserDevice;
import org.jlom.master_upm.tfm.micronaut.dynamic_data.utils.EmbeddedRedisServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
public class ModelTest {
  @Inject
  private IUserDevicesRepository userDevicesRepository;



  private static final Logger LOG = LoggerFactory.getLogger(ModelTest.class);

  @Inject
  EmbeddedRedisServer embeddedRedisServer;

  @BeforeEach
  public void setup() {
    embeddedRedisServer.start();
  }
  @AfterEach
  public void tearDown() {
    embeddedRedisServer.stop();
  }

  @Test
  public void given_ANonExistingEntity_when_adding_then_ShouldWorkFine() {

    UserDevice built = UserDevice.builder()
            .userId(1)
            .devices(Set.of(2L))
            .build();
    userDevicesRepository.add(built);


    UserDevice byUserId = userDevicesRepository.findByUserId(built.getUserId());

    assertThat(byUserId).isNotNull();
    assertThat(byUserId).isEqualTo(built);
  }

  @Test
  public void given_AnExistingEntity_when_adding_then_Should_NOT_Work() {

    UserDevice built = UserDevice.builder()
            .userId(1)
            .devices(Set.of(2L))
            .build();

    boolean added = userDevicesRepository.add(built);
    assertThat(added).isTrue();

    UserDevice builtAgain = UserDevice.builder()
            .userId(1)
            .devices(Set.of(22L))
            .build();

    boolean addedAgain = userDevicesRepository.add(builtAgain);
    assertThat(addedAgain).isFalse();

    UserDevice byUserId = userDevicesRepository.findByUserId(built.getUserId());

    assertThat(byUserId).isNotNull();
    assertThat(byUserId).isEqualTo(built);
    assertThat(byUserId).isNotEqualTo(builtAgain);
  }

  @Test
  public void given_AnExistingEntity_when_updating_then_ShouldWorkFine() {

    UserDevice built = UserDevice.builder()
            .userId(1)
            .devices(Set.of(2L))
            .build();

    boolean added = userDevicesRepository.add(built);
    assertThat(added).isTrue();

    UserDevice builtAgain = UserDevice.builder()
            .userId(1)
            .devices(Set.of(22L))
            .build();

    assertThat(userDevicesRepository.update(builtAgain)).isTrue();


    UserDevice byUserId = userDevicesRepository.findByUserId(built.getUserId());

    assertThat(byUserId).isNotNull();
    assertThat(byUserId).isEqualTo(builtAgain);
    assertThat(byUserId).isNotEqualTo(built);
  }

  @Test
  public void given_ANonExistingEntity_when_updating_then_Should_NOT_Work() {

    UserDevice built = UserDevice.builder()
            .userId(1)
            .devices(Set.of(2L))
            .build();

    boolean added = userDevicesRepository.add(built);
    assertThat(added).isTrue();

    UserDevice builtAgain = UserDevice.builder()
            .userId(2)
            .devices(Set.of(22L))
            .build();

    assertThat(userDevicesRepository.update(builtAgain)).isFalse();


    UserDevice byUserId = userDevicesRepository.findByUserId(built.getUserId());

    assertThat(byUserId).isNotNull();
    assertThat(byUserId).isEqualTo(built);
    assertThat(byUserId).isNotEqualTo(builtAgain);

    UserDevice byUserIdAgain = userDevicesRepository.findByUserId(builtAgain.getUserId());
    assertThat(byUserIdAgain).isNull();

  }

  @Test
  public void given_AnExistingEntity_when_deleting_then_ShouldWorkFine() {

    UserDevice built = UserDevice.builder()
            .userId(1)
            .devices(Set.of(2L))
            .build();

    boolean added = userDevicesRepository.add(built);
    assertThat(added).isTrue();


    Long delete = userDevicesRepository.delete(built.getUserId());
    assertThat(delete).isEqualTo(1);

    UserDevice byUserId = userDevicesRepository.findByUserId(built.getUserId());

    assertThat(byUserId).isNull();
  }

  @Test
  public void given_ANonExistingEntity_when_deleting_then_Should_NOT_WorkFine() {

    UserDevice built = UserDevice.builder()
            .userId(1)
            .devices(Set.of(2L))
            .build();

    boolean added = userDevicesRepository.add(built);
    assertThat(added).isTrue();

    UserDevice builtAgain = UserDevice.builder()
            .userId(2)
            .devices(Set.of(22L))
            .build();

    Long delete = userDevicesRepository.delete(builtAgain.getUserId());
    assertThat(delete).isEqualTo(0);

    UserDevice byUserId = userDevicesRepository.findByUserId(builtAgain.getUserId());
    assertThat(byUserId).isNull();
  }

  @Test
  public void given_AUserWithMultipleDevices_when_lookingForAnyDevice_then_ShouldWork() {

    Set<Long> deviceIds = Set.of(2L, 44L, 3L);

    UserDevice built = UserDevice.builder()
            .userId(1)
            .devices(deviceIds)
            .build();

    boolean added = userDevicesRepository.add(built);
    assertThat(added).isTrue();


    List<UserDevice> userDevices = deviceIds
            .stream()
            .map(userDevicesRepository::findByDeviceId)
            .collect(Collectors.toList());

    assertThat(userDevices).allMatch( ud -> ud.equals(built));

  }

  @Test
  public void listAllTest() {

    UserDevice built = UserDevice.builder()
            .userId(1)
            .devices(Set.of(2L))
            .build();
    assertThat(userDevicesRepository.add(built)).isTrue();

    UserDevice builtAgain = UserDevice.builder()
            .userId(2)
            .devices(Set.of(22L))
            .build();
    assertThat(userDevicesRepository.add(builtAgain)).isTrue();


    assertThat(userDevicesRepository.listAllUsers()).containsOnly(built,builtAgain);
  }
}

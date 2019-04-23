package org.jlom.master_upm.tfm.springboot.dynamic_data.controller;

import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponse;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos.UserDevice;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class ServiceTest {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceTest.class);

  @Autowired
  private UserDeviceService service;

  private static int redisEmbeddedServerPort = 6379;
  private RedisServer redisEmbeddedServer = new RedisServer(redisEmbeddedServerPort);



  @Before
  public void setup() {
    redisEmbeddedServer.start();
    String redisContainerIpAddress = "localhost";
    int redisFirstMappedPort = redisEmbeddedServerPort;
    LOG.info("-=* Redis Container running on: " + redisContainerIpAddress + ":" + redisFirstMappedPort);
  }

  @After
  public void tearDown() {
    redisEmbeddedServer.stop();
  }

  @Test
  public void given_ANonExistingUser_when_CreateUser_then_AllShouldWork() {

    //given
    final long expectedUserId = 1;

    UserDevice expectedUserDevice = UserDevice.builder()
            .userId(expectedUserId)
            .build();

    //when
    UserDeviceServiceResponse response = service.createUser(expectedUserId);

    //then
    assertThat(response).isInstanceOf(UserDeviceServiceResponseOK.class);

    UserDevice actualUserDevice = ((UserDeviceServiceResponseOK) response).getUserDevice();
    assertThat(actualUserDevice).isEqualTo(expectedUserDevice);

  }

  @Test
  public void given_AnExistingUser_when_CreateUser_then_Should_NOT_Work() {

    //given
    final long expectedUserId = 1;

    assertThat(service.createUser(expectedUserId)).isInstanceOf(UserDeviceServiceResponseOK.class);

    //when
    UserDeviceServiceResponse response = service.createUser(expectedUserId);

    //then
    assertThat(response).isInstanceOf(UserDeviceServiceResponseFailureInvalidInputParameter.class);
  }

  @Test
  public void given_AnExistingUser_when_addDevicesToUser_then_AllShouldWork() {

    //given
    final long expectedUserId = 1;
    final Set<Long> initialDevices = Set.of(1L,2L);
    final Set<Long> additionalDevices = Set.of(1L,3L);
    final Long[] expectedDevices = new Long[]{1L, 2L, 3L};

    UserDevice existingUser = UserDevice.builder()
            .userId(expectedUserId)
            .devices(initialDevices)
            .build();

    assertThat(service.createUser(existingUser.getUserId(), existingUser.getDevices()))
            .isInstanceOf(UserDeviceServiceResponseOK.class);

    //when
    UserDeviceServiceResponse response = service.addDevicesToUser(existingUser.getUserId(), additionalDevices);

    //then
    assertThat(response).isInstanceOf(UserDeviceServiceResponseOK.class);
    assertThat(((UserDeviceServiceResponseOK)response).getUserDevice().getDevices())
            .containsExactlyInAnyOrder(expectedDevices);

  }
  @Test
  public void given_ANonExistingUser_when_addDevicesToUser_then_AllShould_NOT_Work() {

    //given
    final long expectedUserId = 1;
    final Set<Long> initialDevices = Set.of(1L,2L);
    final Set<Long> additionalDevices = Set.of(1L,3L);

    UserDevice nonExistingUser = UserDevice.builder()
            .userId(expectedUserId)
            .devices(initialDevices)
            .build();

    //when
    UserDeviceServiceResponse response = service.addDevicesToUser(nonExistingUser.getUserId(), additionalDevices);

    //then
    UserDevice actualUserDevice = ((UserDeviceServiceResponseOK) response).getUserDevice();
    assertThat(actualUserDevice.getUserId()).isEqualTo(nonExistingUser.getUserId());
    assertThat(actualUserDevice.getDevices()).containsOnly(additionalDevices.toArray(new Long[0]));
  }


  @Test
  public void given_AnExistingUser_when_removeDevicesFromUser_then_AllShouldWork() {

    //given
    final long expectedUserId = 1;
    final Set<Long> initialDevices = Set.of(1L,2L,3L);
    final Set<Long> removedDevices = Set.of(1L,3L);
    final Long[] expectedDevices = new Long[]{2L};

    UserDevice existingUser = UserDevice.builder()
            .userId(expectedUserId)
            .devices(initialDevices)
            .build();

    assertThat(service.createUser(existingUser.getUserId(), existingUser.getDevices()))
            .isInstanceOf(UserDeviceServiceResponseOK.class);

    //when
    UserDeviceServiceResponse response = service.removeDevicesFromUser(existingUser.getUserId(), removedDevices);

    //then
    assertThat(response).isInstanceOf(UserDeviceServiceResponseOK.class);
    assertThat(((UserDeviceServiceResponseOK)response).getUserDevice().getDevices())
            .containsExactlyInAnyOrder(expectedDevices);

  }
  @Test
  public void given_ANonExistingUser_when_removeDevicesToUser_then_AllShould_NOT_Work() {

    //given
    final long expectedUserId = 1;
    final Set<Long> initialDevices = Set.of(1L,2L);

    UserDevice nonExistingUser = UserDevice.builder()
            .userId(expectedUserId)
            .devices(initialDevices)
            .build();

    //when
    UserDeviceServiceResponse response = service.addDevicesToUser(nonExistingUser.getUserId(), initialDevices);

    //then
    UserDevice actualUserDevice = ((UserDeviceServiceResponseOK) response).getUserDevice();
    assertThat(actualUserDevice).isEqualTo(nonExistingUser);
  }


  private UserDevice addCheckedUserDevice(long userId, Set<Long> deviceIds) {

    UserDevice userDevice = UserDevice.builder()
            .userId(userId)
            .devices(deviceIds)
            .build();

    UserDeviceServiceResponse response = service.createUser(userDevice.getUserId(),
            userDevice.getDevices());
    assertThat(response).isInstanceOf(UserDeviceServiceResponseOK.class);

    UserDevice actualUserDevice = ((UserDeviceServiceResponseOK) response).getUserDevice();
    assertThat(actualUserDevice).isEqualTo(userDevice);

    return actualUserDevice;
  }
  @Test
  public void getUsers() {
    //given
    final Set<Long> odd_devices = Set.of(1L,3L,5L,7L,9L);
    final Set<Long> even_devices = Set.of(2L,4L,6L,8L,10L);

    UserDevice userDevice_one = addCheckedUserDevice(1, odd_devices);
    UserDevice userDevice_two = addCheckedUserDevice(2, even_devices);

    //when
    UserDevice actualUserDevice_one = service.getUser(1);
    UserDevice actualUserDevice_two = service.getUser(2);

    //then
    assertThat(actualUserDevice_one).isEqualTo(userDevice_one);
    assertThat(actualUserDevice_two).isEqualTo(userDevice_two);

  }

  @Test
  public void getUserForDevice() {
    //given
    final Set<Long> odd_devices = Set.of(1L,3L,5L,7L,9L);
    final Set<Long> even_devices = Set.of(2L,4L,6L,8L,10L);

    addCheckedUserDevice(1,odd_devices);
    addCheckedUserDevice(2,even_devices);

    //when
    boolean odd = odd_devices.stream()
            .map(service::getUserForDevice)
            .allMatch(id -> id.isPresent() && id.get().equals(1L));

    boolean even = even_devices.stream()
            .map(service::getUserForDevice)
            .allMatch(id -> id.isPresent() && id.get().equals(2L));


    //then
    assertThat(odd).isTrue();
    assertThat(even).isTrue();

  }

  @Test
  public void errorCreatingForUserDevice() {
    //given
    final Set<Long> odd_devices = Set.of(1L,3L,5L,7L,9L);
    final Set<Long> even_devices = Set.of(2L,4L,6L,8L,10L);

    addCheckedUserDevice(1,odd_devices);
    addCheckedUserDevice(2,even_devices);

    //when
    HashSet<Long> union = new HashSet<>(odd_devices);
    union.addAll(even_devices);

    List<UserDeviceServiceResponse> responses = union.stream()
            .map(devId -> service.createUser(3, Set.of(devId)))
            .collect(Collectors.toList());


    //then
    boolean allMatch = responses.stream()
                             .allMatch(r -> r instanceof UserDeviceServiceResponseFailureInvalidInputParameter);

    assertThat(allMatch).isTrue();


  }
}
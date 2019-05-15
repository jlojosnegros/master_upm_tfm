package org.jlom.master_upm.tfm.micronaut.dynamic_data.view;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.jlom.master_upm.tfm.micronaut.dynamic_data.controller.UserDeviceService;
import org.jlom.master_upm.tfm.micronaut.dynamic_data.controller.api.dtos.UserDeviceServiceResponse;
import org.jlom.master_upm.tfm.micronaut.dynamic_data.controller.api.dtos.UserDeviceServiceResponseOK;
import org.jlom.master_upm.tfm.micronaut.dynamic_data.model.daos.UserDevice;
import org.jlom.master_upm.tfm.micronaut.dynamic_data.utils.DtosTransformations;
import org.jlom.master_upm.tfm.micronaut.dynamic_data.utils.EmbeddedRedisServer;
import org.jlom.master_upm.tfm.micronaut.dynamic_data.view.api.dtos.InputUserDevice;
import org.jlom.master_upm.tfm.micronaut.dynamic_data.view.api.dtos.ProblemDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;
import static org.jlom.master_upm.tfm.micronaut.dynamic_data.utils.DtosTransformations.serviceToView;
import static org.jlom.master_upm.tfm.micronaut.dynamic_data.utils.JsonUtils.jsonToList;
import static org.jlom.master_upm.tfm.micronaut.dynamic_data.utils.JsonUtils.jsonToObject;


@MicronautTest
public class ViewTest {

  private static final Logger LOG = LoggerFactory.getLogger(ViewTest.class);

  @Inject
  private UserDeviceService service;

  @Inject
  @Client("/")
  private HttpClient client;

  @Inject
  private EmbeddedRedisServer embeddedRedisServer;

  @BeforeEach
  public void setup() {
    embeddedRedisServer.start();
  }
  @AfterEach
  public void tearDown() {
    embeddedRedisServer.stop();
  }


  private void addCheckedUserDevice(long userId, Set<Long> deviceIds) {

    UserDevice userDevice = UserDevice.builder()
            .userId(userId)
            .devices(deviceIds)
            .build();

    UserDeviceServiceResponse response = service.createUser(userDevice.getUserId(),
            userDevice.getDevices());
    assertThat(response).isInstanceOf(UserDeviceServiceResponseOK.class);

    UserDevice actualUserDevice = ((UserDeviceServiceResponseOK) response).getUserDevice();
    assertThat(actualUserDevice).isEqualTo(userDevice);

  }

  private Map<Long,UserDevice> addSomeCheckedUserDevices(final int numberOfElements) {
    Map<Long,UserDevice> users = new HashMap<>(numberOfElements);

    for(int idx = 0; idx <= numberOfElements; idx++) {

      long userId = idx+1;
      Set<Long> devices = LongStream.rangeClosed((idx * numberOfElements) + 1, ((idx + 1) * numberOfElements))
              .boxed()
              .collect(Collectors.toSet());

      users.putIfAbsent(userId,new UserDevice(userId, devices));
      addCheckedUserDevice(userId,devices);
    }

    return users;
  }

  @Test
  public void given_ANonExistingUser_when_addDeviceToUser_then_NewUserIsCreatedAndAllWorksOK() throws IOException {

    final long userId = 1;
    final Set<Long> devices = Set.of(1L,2L);

    InputUserDevice inputUserDevice = InputUserDevice.builder()
            .userId(String.valueOf(userId))
            .devices(devices.stream().map(String::valueOf).collect(Collectors.toSet()))
            .build();

    String body = client.toBlocking()
            .retrieve(HttpRequest.POST("/dynamic-data/user-device/add-devices",inputUserDevice));
    assertThat(body).isNotEmpty();


    LOG.info("response: " + body);

    InputUserDevice actualInputUserDevice = jsonToObject(body, InputUserDevice.class);

    LOG.debug("sent: " + inputUserDevice);
    LOG.debug("recv: " + actualInputUserDevice);

    assertThat(actualInputUserDevice).isEqualTo(inputUserDevice);

  }

  @Test
  public void given_AnExistingUser_when_addDeviceToUser_then_AllWorksOK() throws IOException {

    final long userId = 1;
    final Set<Long> devices = Set.of(1L,2L);
    final Set<Long> additionalDevices = Set.of(3L,2L);

    Set<Long> union = new HashSet<>(devices);
    union.addAll(additionalDevices);

    Set<String> expectedDevices = union.stream().map(String::valueOf).collect(Collectors.toSet());

    addCheckedUserDevice(userId,devices);

    InputUserDevice inputUserDevice = serviceToView(new UserDevice(userId,additionalDevices));

    String body = client.toBlocking()
            .retrieve(HttpRequest.POST("/dynamic-data/user-device/add-devices",
                    inputUserDevice)
            );


    LOG.info("response: " + body);

    InputUserDevice actualInputUserDevice = jsonToObject(body, InputUserDevice.class);

    LOG.debug("sent: " + inputUserDevice);
    LOG.debug("recv: " + actualInputUserDevice);


    assertThat(actualInputUserDevice.getUserId()).isEqualTo(inputUserDevice.getUserId());
    assertThat(actualInputUserDevice.getDevices()).containsOnly(expectedDevices.toArray(new String[0]));
  }

  @Test
  public void given_AnExistingUser_when_removeDeviceFromUser_then_ShouldWorkOk() throws IOException {
    final long userId = 1;
    final Set<Long> devices = Set.of(1L,2L,3L);
    final Set<Long> removedDevices = Set.of(3L,2L);

    Set<Long> remainingDevices = devices.stream()
            .filter( id -> !removedDevices.contains(id))
            .collect(Collectors.toSet());

    final Set<String> expectedDevices = remainingDevices.stream().map(String::valueOf).collect(Collectors.toSet());

    addCheckedUserDevice(userId,devices);

    InputUserDevice inputUserDevice = serviceToView(new UserDevice(userId,removedDevices));

    String body = client.toBlocking()
            .retrieve(HttpRequest.POST("/dynamic-data/user-device/remove-devices",
                    inputUserDevice)
            );
    assertThat(body).isNotEmpty();

    LOG.info("jlom: response: " + body);

    InputUserDevice actualInputUserDevice = jsonToObject(body, InputUserDevice.class);

    LOG.debug("sent: " + inputUserDevice);
    LOG.debug("recv: " + actualInputUserDevice);


    assertThat(actualInputUserDevice.getUserId()).isEqualTo(inputUserDevice.getUserId());
    assertThat(actualInputUserDevice.getDevices()).containsOnly(expectedDevices.toArray(new String[0]));

  }

  @Test
  public void given_ANonExistingUser_when_removeDeviceFromUser_then_Should_NOT_Work() throws IOException {
    final long userId = 1;
    final Set<Long> removedDevices = Set.of(3L,2L);

    InputUserDevice inputUserDevice = serviceToView(new UserDevice(userId,removedDevices));


    try {
      client.toBlocking()
              .retrieve(HttpRequest.POST("/dynamic-data/user-device/remove-devices", inputUserDevice)
              );
    }
    catch(HttpClientResponseException ex) {
      assertThat(ex.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());

      String body = (String)ex.getResponse().body();
      assertThat(body).isNotEmpty();

      LOG.debug("response: " + body);
      ProblemDetails problemDetails = jsonToObject(body, ProblemDetails.class);
      LOG.info("problem: " + problemDetails);
      return;
    }
    fail("An exception should have been thrown");

  }

  @Test
  public void given_SomePreCreatedUsers_when_listAllUsers_then_AllUsersShouldBeListed() throws IOException {
    final int numberOfElements = 5;

    Map<Long,UserDevice> users = addSomeCheckedUserDevices(numberOfElements);

    String body = client.toBlocking().retrieve(HttpRequest.GET("/dynamic-data/user-device/users"));
    assertThat(body).isNotEmpty();

    LOG.info("response: " + body);

    List<InputUserDevice> inputUserDevices = jsonToList(body, InputUserDevice.class);

    LOG.error("recv: " + inputUserDevices);
    assertThat(inputUserDevices).containsOnly(
            users.values().stream().map(DtosTransformations::serviceToView)
                    .toArray(InputUserDevice[]::new)
    );

  }

  @Test
  public void given_SomePreCreatedUsersWithDevices_when_getDevicesByUser_then_EachUserShouldHaveItsDevices() throws IOException {

    final int numberOfElements = 5;
    Map<Long,UserDevice> users = addSomeCheckedUserDevices(numberOfElements);

    for (int userId = 1; userId <= numberOfElements; userId++) {

      String body = client.toBlocking().retrieve(HttpRequest.GET("/dynamic-data/user-device/users/" + userId));
      assertThat(body).isNotEmpty();

      LOG.info("response: " + body);

      InputUserDevice inputUserDevice = jsonToObject(body, InputUserDevice.class);
      LOG.error("recv: " + inputUserDevice);

      assertThat(inputUserDevice).isEqualTo(serviceToView(users.get((long)userId)));
    }
  }

  @Test
  public void given_SomePreCreatedUsersWithDevices_when_getUserForDevice_then_ShouldWorkOK() throws IOException {

    final int numberOfElements = 5;
    Map<Long, UserDevice> users = addSomeCheckedUserDevices(numberOfElements);


    for (UserDevice user : users.values()) {
      for (var deviceId : user.getDevices()) {
        String body = client.toBlocking().retrieve(HttpRequest.GET("/dynamic-data/user-device/device/" + deviceId));
        assertThat(body).isNotEmpty();

        LOG.info("response: " + body);

        InputUserDevice inputUserDevice = jsonToObject(body, InputUserDevice.class);
        LOG.error("recv: " + inputUserDevice);
        assertThat(inputUserDevice).isEqualTo(serviceToView(user));

      }
    }
  }
}

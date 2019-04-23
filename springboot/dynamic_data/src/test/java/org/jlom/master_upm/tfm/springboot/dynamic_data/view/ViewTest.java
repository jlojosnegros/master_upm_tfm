package org.jlom.master_upm.tfm.springboot.dynamic_data.view;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.UserDeviceService;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponse;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos.UserDevice;
import org.jlom.master_upm.tfm.springboot.dynamic_data.utils.DtosTransformations;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.api.dtos.InputUserDevice;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.api.dtos.ProblemDetails;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.xmlunit.builder.Input;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jlom.master_upm.tfm.springboot.dynamic_data.utils.DtosTransformations.serviceToView;
import static org.jlom.master_upm.tfm.springboot.dynamic_data.utils.JsonUtils.ObjectToJson;
import static org.jlom.master_upm.tfm.springboot.dynamic_data.utils.JsonUtils.retrieveListOfResourcesFromResponse;
import static org.jlom.master_upm.tfm.springboot.dynamic_data.utils.JsonUtils.retrieveResourceFromResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ViewTest {

  @LocalServerPort
  private int port;

  @Autowired
  private UserDeviceService service;

  private static final Logger LOG = LoggerFactory.getLogger(ViewTest.class);

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


  private HttpResponse getRestResponseTo(String resourceUri) throws IOException {
    HttpClient client = HttpClientBuilder.create().build();
    return client.execute(new HttpGet("http://localhost:"+port+resourceUri));
  }


  private HttpPost createPostRequest(String resourceUri) {
    HttpPost postRequest = new HttpPost("http://localhost:"+port+resourceUri);
    postRequest.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return postRequest;
  }

  private HttpResponse postMessageTo(String resourceUri) throws IOException {

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPost postRequest = createPostRequest(resourceUri);
    return httpClient.execute(postRequest);
  }



  private HttpResponse postMessageTo(String resourceUri, Object body) throws IOException {

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPost postRequest = createPostRequest(resourceUri);

    String jsonBody = ObjectToJson(body);
    StringEntity entityBody = new StringEntity(jsonBody);
    postRequest.setEntity(entityBody);

    return httpClient.execute(postRequest);
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

    HttpResponse httpResponse = postMessageTo("/dynamic-data/user-device/add-devices", inputUserDevice);


    int statusCode = httpResponse.getStatusLine().getStatusCode();
    assertThat(statusCode).isEqualTo(HttpStatus.OK.value());

    LOG.error("jlom: response: " + httpResponse);

    InputUserDevice actualInputUserDevice = retrieveResourceFromResponse(httpResponse, InputUserDevice.class);

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

    HttpResponse httpResponse = postMessageTo("/dynamic-data/user-device/add-devices", inputUserDevice);


    int statusCode = httpResponse.getStatusLine().getStatusCode();
    assertThat(statusCode).isEqualTo(HttpStatus.OK.value());

    LOG.error("jlom: response: " + httpResponse);

    InputUserDevice actualInputUserDevice = retrieveResourceFromResponse(httpResponse, InputUserDevice.class);

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

    HttpResponse httpResponse = postMessageTo("/dynamic-data/user-device/remove-devices", inputUserDevice);


    int statusCode = httpResponse.getStatusLine().getStatusCode();
    assertThat(statusCode).isEqualTo(HttpStatus.OK.value());

    LOG.error("jlom: response: " + httpResponse);

    InputUserDevice actualInputUserDevice = retrieveResourceFromResponse(httpResponse, InputUserDevice.class);

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

    HttpResponse httpResponse = postMessageTo("/dynamic-data/user-device/remove-devices", inputUserDevice);


    int statusCode = httpResponse.getStatusLine().getStatusCode();
    assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());

    LOG.debug("response: " + httpResponse);

    ProblemDetails problemDetails = retrieveResourceFromResponse(httpResponse, ProblemDetails.class);

    LOG.info("problem: " + problemDetails);

  }

  @Test
  public void given_SomePreCreatedUsers_when_listAllUsers_then_AllUsersShouldBeListed() throws IOException {
    final int numberOfElements = 5;

    Map<Long,UserDevice> users = addSomeCheckedUserDevices(numberOfElements);

    HttpResponse httpResponse = getRestResponseTo("/dynamic-data/user-device/users");
    int statusCode = httpResponse.getStatusLine().getStatusCode();
    LOG.error("response: " + httpResponse);
    assertThat(statusCode).isEqualTo(HttpStatus.OK.value());

    List<InputUserDevice> inputUserDevices = retrieveListOfResourcesFromResponse(httpResponse, InputUserDevice.class);

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
      HttpResponse httpResponse = getRestResponseTo("/dynamic-data/user-device/users/" + userId);
      LOG.error("response: " + httpResponse);

      int statusCode = httpResponse.getStatusLine().getStatusCode();
      assertThat(statusCode).isEqualTo(HttpStatus.OK.value());

      InputUserDevice inputUserDevice = retrieveResourceFromResponse(httpResponse, InputUserDevice.class);
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
        HttpResponse httpResponse = getRestResponseTo("/dynamic-data/user-device/device/" + deviceId);
        LOG.error("response: " + httpResponse);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.OK.value());

        InputUserDevice inputUserDevice = retrieveResourceFromResponse(httpResponse, InputUserDevice.class);
        LOG.error("recv: " + inputUserDevice);

        assertThat(inputUserDevice).isEqualTo(serviceToView(user));

      }
    }
  }
}

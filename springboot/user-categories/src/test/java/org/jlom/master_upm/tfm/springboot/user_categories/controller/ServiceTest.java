package org.jlom.master_upm.tfm.springboot.user_categories.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.StreamControlServiceResponse;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.StreamControlServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.StreamControlServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.out.OutBoundNotifications;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.out.StreamControlStreamingNotification;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.clients.InputUserDevice;
import org.jlom.master_upm.tfm.springboot.user_categories.model.api.IStreamControlRepository;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.StreamStatus;
import org.jlom.master_upm.tfm.springboot.user_categories.utils.JsonUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8080)
@ActiveProfiles("test")
public class ServiceTest {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceTest.class);

  @Autowired
  private StreamControlService service;

  @Autowired
  private IStreamControlRepository repository;

  @Autowired
  private OutBoundNotifications outBoundNotifications;

  @Autowired
  private MessageCollector messageCollector;

  private static int redisEmbeddedServerPort = 6379;
  private RedisServer redisEmbeddedServer = new RedisServer(redisEmbeddedServerPort);

//  @Rule
//  public WireMockRule wireMockRule = new WireMockRule(8080); // No-args constructor defaults to port 8080

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

  private StreamControlData addCheckedStreamControlData(long userId,
                                                        long deviceId,
                                                        long streamId,
                                                        StreamStatus status,
                                                        boolean tillTheEnd) {
    StreamControlData streamControlData = StreamControlData.builder()
            .userId(userId)
            .deviceId(deviceId)
            .streamId(streamId)
            .status(status)
            .tillTheEnd(tillTheEnd)
            .build();
    repository.save(streamControlData);

    StreamControlData userRunning = repository.isUserRunning(userId);
    StreamControlData deviceRunning = repository.isDeviceRunning(deviceId);
    assertThat(userRunning).isEqualTo(deviceRunning);
    return streamControlData;
  }

  @Test
  public void given_NoStreamingRunning_when_PlayANewStream_then_AllShouldWork() throws JsonProcessingException {

    final long userId = 1;
    final long streamId = 1;
    final long deviceId = 1;

    final String uri = String.format("/dynamic-data/user-device/device/%d",deviceId);


    InputUserDevice userDevice = InputUserDevice.builder()
            .userId(String.valueOf(userId))
            .devices(Set.of(String.valueOf(deviceId)))
            .build();

    stubFor(get(urlEqualTo(uri))
            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(JsonUtils.ObjectToJson(userDevice))));


    StreamControlServiceResponse play = service.play(streamId, deviceId);

    assertThat(play).isInstanceOf(StreamControlServiceResponseOK.class);
    StreamControlData streamControlData = ((StreamControlServiceResponseOK) play).getStreamControlData();

    assertThat(streamControlData.getDeviceId()).isEqualTo(deviceId);
    assertThat(streamControlData.getStatus()).isEqualTo(StreamStatus.RUNNING);
    assertThat(streamControlData.getStreamId()).isEqualTo(streamId);


    final List<Message> listOfNotifications = new LinkedList<>();
    messageCollector.forChannel(outBoundNotifications.streamingNotifications()).drainTo(listOfNotifications);

    assertThat(listOfNotifications).hasSize(1);
    Object payload = listOfNotifications.get(0).getPayload();
    LOG.error("jlom: payload:" + payload.toString());
    StreamControlStreamingNotification notification = null;
    try {
      notification = JsonUtils.jsonToObject((String) payload, StreamControlStreamingNotification.class);
    } catch (IOException e) {
      e.printStackTrace();
      fail("Unable con convert payload to notification: " + payload.toString());
    }
    assertThat(notification.getUserId()).isEqualTo(String.valueOf(streamControlData.getUserId()));
    assertThat(notification.getDeviceId()).isEqualTo(String.valueOf(streamControlData.getDeviceId()));
    assertThat(notification.getStreamId()).isEqualTo(String.valueOf(streamControlData.getStreamId()));
    assertThat(notification.getOperation()).isEqualTo(StreamControlStreamingNotification.Operation.PLAY);

  }

  @Test
  public void given_AStreamingRunning_when_PlayANewStream_then_AllShould_NOT_Work() throws JsonProcessingException {

    final long userId = 1;
    final long streamId = 1;
    final long anotherStreamId = 2;
    final long deviceId = 1;
    final String uri = String.format("/dynamic-data/user-device/device/%d",deviceId);

    InputUserDevice userDevice = InputUserDevice.builder()
            .userId(String.valueOf(userId))
            .devices(Set.of(String.valueOf(deviceId)))
            .build();

    stubFor(get(urlEqualTo(uri))
            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(JsonUtils.ObjectToJson(userDevice))));

    StreamControlData alreadyRunning = addCheckedStreamControlData(userId,
            deviceId,
            anotherStreamId,
            StreamStatus.RUNNING,
            false);


    StreamControlServiceResponse response = service.play(streamId, deviceId);

    assertThat(response).isInstanceOf(StreamControlServiceResponseFailureInvalidInputParameter.class);
    StreamControlServiceResponseFailureInvalidInputParameter invalidResponse = (StreamControlServiceResponseFailureInvalidInputParameter) response;
    String paramName = invalidResponse.getParamName();
    Object paramValue = invalidResponse.getParamValue();

    assertThat(paramName).isEqualToIgnoringCase("deviceId");
    assertThat(paramValue).isEqualTo(deviceId);

    final List<Message> listOfNotifications = new LinkedList<>();
    messageCollector.forChannel(outBoundNotifications.streamingNotifications()).drainTo(listOfNotifications);

    assertThat(listOfNotifications).hasSize(0);
  }

  @Test
  public void given_AStreamingRunning_when_StopAExistingStream_then_AllShouldWork() {

    final long userId = 1;
    final long streamId = 1;
    final long anotherStreamId = 2;
    final long deviceId = 1;

    StreamControlData alreadyRunning = addCheckedStreamControlData(userId,
            deviceId,
            anotherStreamId,
            StreamStatus.RUNNING,
            false);

    StreamControlServiceResponse response = service.stop(deviceId);

    assertThat(response).isInstanceOf(StreamControlServiceResponseOK.class);
    StreamControlServiceResponseOK responseOK = (StreamControlServiceResponseOK) response;
    alreadyRunning.setStatus(StreamStatus.DONE);
    assertThat(responseOK.getStreamControlData()).isEqualTo(alreadyRunning);

    final List<Message> listOfNotifications = new LinkedList<>();
    messageCollector.forChannel(outBoundNotifications.streamingNotifications()).drainTo(listOfNotifications);

    assertThat(listOfNotifications).hasSize(1);
    Object payload = listOfNotifications.get(0).getPayload();
    LOG.error("jlom: payload:" + payload.toString());
    StreamControlStreamingNotification notification = null;
    try {
      notification = JsonUtils.jsonToObject((String) payload, StreamControlStreamingNotification.class);
    } catch (IOException e) {
      e.printStackTrace();
      fail("Unable con convert payload to notification: " + payload.toString());
    }
    assertThat(notification.getUserId()).isEqualTo(String.valueOf(alreadyRunning.getUserId()));
    assertThat(notification.getDeviceId()).isEqualTo(String.valueOf(alreadyRunning.getDeviceId()));
    assertThat(notification.getStreamId()).isEqualTo(String.valueOf(alreadyRunning.getStreamId()));
    assertThat(notification.getOperation()).isEqualTo(StreamControlStreamingNotification.Operation.STOP);

  }

  @Test
  public void given_NoStreamingRunning_when_StopANonExistingStream_then_AllShould_NOT_Work() throws JsonProcessingException {

    final long deviceId = 1;

    StreamControlServiceResponse response = service.stop(deviceId);

    assertThat(response).isInstanceOf(StreamControlServiceResponseFailureInvalidInputParameter.class);
    StreamControlServiceResponseFailureInvalidInputParameter invalidResponse = (StreamControlServiceResponseFailureInvalidInputParameter) response;
    String paramName = invalidResponse.getParamName();
    Object paramValue = invalidResponse.getParamValue();

    assertThat(paramName).isEqualToIgnoringCase("deviceId");
    assertThat(paramValue).isEqualTo(deviceId);

    final List<Message> listOfNotifications = new LinkedList<>();
    messageCollector.forChannel(outBoundNotifications.streamingNotifications()).drainTo(listOfNotifications);

    assertThat(listOfNotifications).hasSize(0);

  }

  @Test
  public void given_AStreamingRunning_when_PauseAExistingStream_then_AllShouldWork() {

    final long userId = 1;
    final long anotherStreamId = 2;
    final long deviceId = 1;

    StreamControlData alreadyRunning = addCheckedStreamControlData(userId,
            deviceId,
            anotherStreamId,
            StreamStatus.RUNNING,
            false);

    StreamControlServiceResponse response = service.pause(deviceId);

    assertThat(response).isInstanceOf(StreamControlServiceResponseOK.class);
    StreamControlServiceResponseOK responseOK = (StreamControlServiceResponseOK) response;
    alreadyRunning.setStatus(StreamStatus.PAUSED);
    assertThat(responseOK.getStreamControlData()).isEqualTo(alreadyRunning);

    final List<Message> listOfNotifications = new LinkedList<>();
    messageCollector.forChannel(outBoundNotifications.streamingNotifications()).drainTo(listOfNotifications);

    assertThat(listOfNotifications).hasSize(1);
    Object payload = listOfNotifications.get(0).getPayload();
    LOG.error("jlom: payload:" + payload.toString());
    StreamControlStreamingNotification notification = null;
    try {
      notification = JsonUtils.jsonToObject((String) payload, StreamControlStreamingNotification.class);
    } catch (IOException e) {
      e.printStackTrace();
      fail("Unable con convert payload to notification: " + payload.toString());
    }
    assertThat(notification.getUserId()).isEqualTo(String.valueOf(alreadyRunning.getUserId()));
    assertThat(notification.getDeviceId()).isEqualTo(String.valueOf(alreadyRunning.getDeviceId()));
    assertThat(notification.getStreamId()).isEqualTo(String.valueOf(alreadyRunning.getStreamId()));
    assertThat(notification.getOperation()).isEqualTo(StreamControlStreamingNotification.Operation.PAUSE);

  }

  @Test
  public void given_NoStreamingRunning_when_PauseANonExistingStream_then_AllShould_NOT_Work() throws JsonProcessingException {

    final long deviceId = 1;

    StreamControlServiceResponse response = service.pause(deviceId);

    assertThat(response).isInstanceOf(StreamControlServiceResponseFailureInvalidInputParameter.class);
    StreamControlServiceResponseFailureInvalidInputParameter invalidResponse = (StreamControlServiceResponseFailureInvalidInputParameter) response;
    String paramName = invalidResponse.getParamName();
    Object paramValue = invalidResponse.getParamValue();

    assertThat(paramName).isEqualToIgnoringCase("deviceId");
    assertThat(paramValue).isEqualTo(deviceId);

    final List<Message> listOfNotifications = new LinkedList<>();
    messageCollector.forChannel(outBoundNotifications.streamingNotifications()).drainTo(listOfNotifications);

    assertThat(listOfNotifications).hasSize(0);
  }
}
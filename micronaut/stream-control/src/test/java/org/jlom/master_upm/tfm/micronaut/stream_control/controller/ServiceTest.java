package org.jlom.master_upm.tfm.micronaut.stream_control.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.test.annotation.MicronautTest;
import org.jetbrains.annotations.NotNull;
import org.jlom.master_upm.tfm.micronaut.stream_control.controller.api.dtos.StreamControlServiceResponse;
import org.jlom.master_upm.tfm.micronaut.stream_control.controller.api.dtos.StreamControlServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.micronaut.stream_control.controller.api.dtos.StreamControlServiceResponseOK;
import org.jlom.master_upm.tfm.micronaut.stream_control.controller.api.out.StreamControlStreamingNotification;
import org.jlom.master_upm.tfm.micronaut.stream_control.controller.clients.InputUserDevice;
import org.jlom.master_upm.tfm.micronaut.stream_control.model.api.IStreamControlRepository;
import org.jlom.master_upm.tfm.micronaut.stream_control.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.micronaut.stream_control.model.daos.StreamStatus;
import org.jlom.master_upm.tfm.micronaut.stream_control.rabbitmq.TestListener;
import org.jlom.master_upm.tfm.micronaut.stream_control.utils.EmbeddedRedisServer;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jlom.master_upm.tfm.micronaut.stream_control.utils.JsonUtils.ObjectToJson;


@MicronautTest
public class ServiceTest {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceTest.class);

//  @Rule
//  public WireMockRule dynamicServerMock = new WireMockRule(8080);

  static WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(8080));

  @Inject
  private StreamControlService service;

  @Inject
  private IStreamControlRepository repository;


  @Inject
  private TestListener notificationListener;


//  @Rule
//  public GenericContainer rabbitmq = new GenericContainer<>("library/rabbitmq:3.7")
//          .withExposedPorts(5672)
//          .waitingFor(new LogMessageWaitStrategy().withRegEx("(?s).*Server startup complete.*"));


//  static GenericContainer rabbitmq = new GenericContainer<>("library/rabbitmq:3.7")
//          .withExposedPorts(5672)
//          .waitingFor(new LogMessageWaitStrategy().withRegEx("(?s).*Server startup complete.*"));
//
//  static {
//    rabbitmq.start();
//  }

  @Inject
  EmbeddedRedisServer embeddedRedisServer;

  @BeforeAll
  public static void init() {
    wireMockServer.start();
  }

  @AfterAll
  public  static void shudown() {
    wireMockServer.stop();
  }

  @BeforeEach
  public void setup() {
    embeddedRedisServer.start();
    notificationListener.cleanNotifications();
    wireMockServer.resetAll();
  }

  @AfterEach
  public void tearDown() {
    notificationListener.cleanNotifications();
    embeddedRedisServer.stop();
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

//    ApplicationContext applicationContext = ApplicationContext.run(
//            Map.of("rabbitmq.port", rabbitmq.getMappedPort(5672))
//            , "test");
//    ApplicationContext applicationContext = ApplicationContext.run();
//    TestListener notificationListener = applicationContext.getBean(TestListener.class);

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
                    .withStatus(HttpStatus.OK.getCode())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                    .withBody(ObjectToJson(userDevice))));


    StreamControlServiceResponse play = service.play(streamId, deviceId);

    assertThat(play).isInstanceOf(StreamControlServiceResponseOK.class);
    StreamControlData streamControlData = ((StreamControlServiceResponseOK) play).getStreamControlData();

    assertThat(streamControlData.getDeviceId()).isEqualTo(deviceId);
    assertThat(streamControlData.getStatus()).isEqualTo(StreamStatus.RUNNING);
    assertThat(streamControlData.getStreamId()).isEqualTo(streamId);


    List<StreamControlStreamingNotification> listOfNotifications = getNotifications();


    assertThat(listOfNotifications).hasSize(1);
    StreamControlStreamingNotification notification = listOfNotifications.get(0);
    assertThat(notification.getUserId()).isEqualTo(String.valueOf(streamControlData.getUserId()));
    assertThat(notification.getDeviceId()).isEqualTo(String.valueOf(streamControlData.getDeviceId()));
    assertThat(notification.getStreamId()).isEqualTo(String.valueOf(streamControlData.getStreamId()));
    assertThat(notification.getOperation()).isEqualTo(StreamControlStreamingNotification.Operation.PLAY);

  }

  @Test
  public void given_AStreamingRunning_when_PlayANewStream_then_AllShould_NOT_Work() throws JsonProcessingException {
//    ApplicationContext applicationContext = ApplicationContext.run(
//            Map.of("rabbitmq.port", rabbitmq.getMappedPort(5672))
//            , "test");
//    TestListener notificationListener = applicationContext.getBean(TestListener.class);

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
                    .withStatus(HttpStatus.OK.getCode())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                    .withBody(ObjectToJson(userDevice))));

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

    List<StreamControlStreamingNotification> listOfNotifications = getNotifications();

    assertThat(listOfNotifications).hasSize(0);
  }

  @Test
  public void given_AStreamingRunning_when_StopAExistingStream_then_AllShouldWork() {
//    ApplicationContext applicationContext = ApplicationContext.run(
//            Map.of("rabbitmq.port", rabbitmq.getMappedPort(5672))
//            , "test");
//    TestListener notificationListener = applicationContext.getBean(TestListener.class);

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


    List<StreamControlStreamingNotification> listOfNotifications = getNotifications();
    assertThat(listOfNotifications).hasSize(1);
    var notification = listOfNotifications.get(0);

    assertThat(notification.getUserId()).isEqualTo(String.valueOf(alreadyRunning.getUserId()));
    assertThat(notification.getDeviceId()).isEqualTo(String.valueOf(alreadyRunning.getDeviceId()));
    assertThat(notification.getStreamId()).isEqualTo(String.valueOf(alreadyRunning.getStreamId()));
    assertThat(notification.getOperation()).isEqualTo(StreamControlStreamingNotification.Operation.STOP);

  }

  @NotNull
  private List<StreamControlStreamingNotification> getNotifications() {
    List<StreamControlStreamingNotification> listOfNotifications;
    int cnt = 0;
    do {
      listOfNotifications = notificationListener.getNotifications();
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      cnt++;
    }while (listOfNotifications.size()==0 && cnt <10);

    LOG.error("cnt:"+cnt);
    return listOfNotifications;
  }

  @Test
  public void given_NoStreamingRunning_when_StopANonExistingStream_then_AllShould_NOT_Work() throws JsonProcessingException {
//    ApplicationContext applicationContext = ApplicationContext.run(
//            Map.of("rabbitmq.port", rabbitmq.getMappedPort(5672))
//            , "test");
//    TestListener notificationListener = applicationContext.getBean(TestListener.class);

    final long deviceId = 1;

    StreamControlServiceResponse response = service.stop(deviceId);

    assertThat(response).isInstanceOf(StreamControlServiceResponseFailureInvalidInputParameter.class);
    StreamControlServiceResponseFailureInvalidInputParameter invalidResponse = (StreamControlServiceResponseFailureInvalidInputParameter) response;
    String paramName = invalidResponse.getParamName();
    Object paramValue = invalidResponse.getParamValue();

    assertThat(paramName).isEqualToIgnoringCase("deviceId");
    assertThat(paramValue).isEqualTo(deviceId);

    var listOfNotifications = getNotifications();

    assertThat(listOfNotifications).hasSize(0);

  }

  @Test
  public void given_AStreamingRunning_when_PauseAExistingStream_then_AllShouldWork() {
//    ApplicationContext applicationContext = ApplicationContext.run(
//            Map.of("rabbitmq.port", rabbitmq.getMappedPort(5672))
//            , "test");
//    TestListener notificationListener = applicationContext.getBean(TestListener.class);

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

    var listOfNotifications = getNotifications();

    assertThat(listOfNotifications).hasSize(1);
    var notification = listOfNotifications.get(0);

    assertThat(notification.getUserId()).isEqualTo(String.valueOf(alreadyRunning.getUserId()));
    assertThat(notification.getDeviceId()).isEqualTo(String.valueOf(alreadyRunning.getDeviceId()));
    assertThat(notification.getStreamId()).isEqualTo(String.valueOf(alreadyRunning.getStreamId()));
    assertThat(notification.getOperation()).isEqualTo(StreamControlStreamingNotification.Operation.PAUSE);

  }

  @Test
  public void given_NoStreamingRunning_when_PauseANonExistingStream_then_AllShould_NOT_Work() throws JsonProcessingException {
//    ApplicationContext applicationContext = ApplicationContext.run(
//            Map.of("rabbitmq.port", rabbitmq.getMappedPort(5672))
//            , "test");
//    TestListener notificationListener = applicationContext.getBean(TestListener.class);

    final long deviceId = 1;

    StreamControlServiceResponse response = service.pause(deviceId);

    assertThat(response).isInstanceOf(StreamControlServiceResponseFailureInvalidInputParameter.class);
    StreamControlServiceResponseFailureInvalidInputParameter invalidResponse = (StreamControlServiceResponseFailureInvalidInputParameter) response;
    String paramName = invalidResponse.getParamName();
    Object paramValue = invalidResponse.getParamValue();

    assertThat(paramName).isEqualToIgnoringCase("deviceId");
    assertThat(paramValue).isEqualTo(deviceId);

    var listOfNotifications = getNotifications();
    assertThat(listOfNotifications).hasSize(0);
  }
}
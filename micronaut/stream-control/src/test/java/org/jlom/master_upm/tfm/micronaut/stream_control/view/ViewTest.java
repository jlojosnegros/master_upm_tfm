package org.jlom.master_upm.tfm.micronaut.stream_control.view;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.test.annotation.MicronautTest;
import org.jlom.master_upm.tfm.micronaut.stream_control.controller.clients.InputUserDevice;
import org.jlom.master_upm.tfm.micronaut.stream_control.model.api.IStreamControlRepository;
import org.jlom.master_upm.tfm.micronaut.stream_control.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.micronaut.stream_control.model.daos.StreamStatus;
import org.jlom.master_upm.tfm.micronaut.stream_control.utils.EmbeddedRedisServer;
import org.jlom.master_upm.tfm.micronaut.stream_control.view.api.StreamControlInterface;
import org.jlom.master_upm.tfm.micronaut.stream_control.view.api.dtos.StreamControlReturnValue;
import org.jlom.master_upm.tfm.micronaut.stream_control.view.api.dtos.StreamControlReturnValueError;
import org.jlom.master_upm.tfm.micronaut.stream_control.view.api.dtos.StreamControlReturnValueOk;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jlom.master_upm.tfm.micronaut.stream_control.utils.JsonUtils.ObjectToJson;


@MicronautTest
public class ViewTest {

  private static final Logger LOG = LoggerFactory.getLogger(ViewTest.class);

  @Rule
  public WireMockRule dynamicServerMock = new WireMockRule();

//  @Rule
//  public GenericContainer rabbitmq = new GenericContainer<>("library/rabbitmq:3.7")
//          .withExposedPorts(5672)
//          .waitingFor(new LogMessageWaitStrategy().withRegEx("(?s).*Server startup complete.*"));

  @Inject
  private StreamControlInterface view;

  @Inject
  private IStreamControlRepository repository;


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
                    .withStatus(HttpStatus.OK.getCode())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                    .withBody(ObjectToJson(userDevice))));


    StreamControlReturnValue returnValue = view.play(streamId, deviceId);

    assertThat(returnValue).isInstanceOf(StreamControlReturnValueOk.class);
    StreamControlReturnValueOk returnValueOk = ((StreamControlReturnValueOk) returnValue);

    assertThat(returnValueOk.getUserId()).isEqualTo(String.valueOf(userId));
    assertThat(returnValueOk.getDeviceId()).isEqualTo(String.valueOf(deviceId));
    assertThat(returnValueOk.getStreamId()).isEqualTo(String.valueOf(streamId));
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
                    .withStatus(HttpStatus.OK.getCode())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                    .withBody(ObjectToJson(userDevice))));

    StreamControlData alreadyRunning = addCheckedStreamControlData(userId,
            deviceId,
            anotherStreamId,
            StreamStatus.RUNNING,
            false);


    StreamControlReturnValue returnValue = view.play(streamId, deviceId);
    LOG.error("returnValue: " + returnValue);

    assertThat(returnValue).isInstanceOf(StreamControlReturnValueError.class);
    StreamControlReturnValueError invalidResponse = (StreamControlReturnValueError) returnValue;
    assertThat(invalidResponse.getMessage()).startsWith("Invalid");
    assertThat(invalidResponse.getErrorCode()).isEqualTo(StreamControlReturnValueError.ErrorCode.INVALID_PARAMETER);

  }

  @Test
  public void given_AStreamingRunning_when_StopAExistingStream_then_AllShouldWork() {

    final long userId = 1;
    final long streamId = 1;
    final long anotherStreamId = 1;
    final long deviceId = 1;

    StreamControlData alreadyRunning = addCheckedStreamControlData(userId,
            deviceId,
            anotherStreamId,
            StreamStatus.RUNNING,
            false);

    StreamControlReturnValue returnValue = view.stop(deviceId);

    assertThat(returnValue).isInstanceOf(StreamControlReturnValueOk.class);
    StreamControlReturnValueOk returnValueOk = (StreamControlReturnValueOk) returnValue;

    assertThat(returnValueOk.getUserId()).isEqualTo(String.valueOf(userId));
    assertThat(returnValueOk.getDeviceId()).isEqualTo(String.valueOf(deviceId));
    assertThat(returnValueOk.getStreamId()).isEqualTo(String.valueOf(streamId));

  }

  @Test
  public void given_NoStreamingRunning_when_StopANonExistingStream_then_AllShould_NOT_Work() throws JsonProcessingException {

    final long deviceId = 1;

    StreamControlReturnValue returnValue = view.stop(deviceId);
    LOG.error("returnValue: " + returnValue);

    assertThat(returnValue).isInstanceOf(StreamControlReturnValueError.class);
    StreamControlReturnValueError invalidResponse = (StreamControlReturnValueError) returnValue;
    assertThat(invalidResponse.getMessage()).startsWith("Invalid");
    assertThat(invalidResponse.getErrorCode()).isEqualTo(StreamControlReturnValueError.ErrorCode.INVALID_PARAMETER);
  }

  @Test
  public void given_AStreamingRunning_when_PauseAExistingStream_then_AllShouldWork() {

    final long userId = 1;
    final long deviceId = 1;
    final long streamId = 1;

    StreamControlData alreadyRunning = addCheckedStreamControlData(userId,
            deviceId,
            streamId,
            StreamStatus.RUNNING,
            false);

    StreamControlReturnValue returnValue = view.pause(deviceId);

    assertThat(returnValue).isInstanceOf(StreamControlReturnValueOk.class);
    StreamControlReturnValueOk returnValueOk = (StreamControlReturnValueOk) returnValue;

    assertThat(returnValueOk.getUserId()).isEqualTo(String.valueOf(userId));
    assertThat(returnValueOk.getDeviceId()).isEqualTo(String.valueOf(deviceId));
    assertThat(returnValueOk.getStreamId()).isEqualTo(String.valueOf(streamId));

  }

  @Test
  public void given_NoStreamingRunning_when_PauseANonExistingStream_then_AllShould_NOT_Work() throws JsonProcessingException {

    final long deviceId = 1;

    StreamControlReturnValue returnValue = view.pause(deviceId);

    assertThat(returnValue).isInstanceOf(StreamControlReturnValueError.class);
    StreamControlReturnValueError invalidResponse = (StreamControlReturnValueError) returnValue;
    assertThat(invalidResponse.getMessage()).startsWith("Invalid");
    assertThat(invalidResponse.getErrorCode()).isEqualTo(StreamControlReturnValueError.ErrorCode.INVALID_PARAMETER);
  }
}

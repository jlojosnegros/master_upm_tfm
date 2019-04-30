package org.jlom.master_upm.tfm.springboot.recommendations.view;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jlom.master_upm.tfm.springboot.recommendations.model.api.IRecommendationsRepository;
import org.jlom.master_upm.tfm.springboot.recommendations.view.api.StreamControlInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.jlom.master_upm.tfm.springboot.recommendations.utils.JsonUtils.ObjectToJson;



@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8080)
@ActiveProfiles("test")
public class ViewTest {

  @Autowired
  private StreamControlInterface view;

  @LocalServerPort
  private int port;

  @Autowired
  private IRecommendationsRepository repository;

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

//  private StreamControlData addCheckedStreamControlData(long userId,
//                                                        long deviceId,
//                                                        long streamId,
//                                                        StreamStatus status,
//                                                        boolean tillTheEnd) {
//    StreamControlData streamControlData = StreamControlData.builder()
//            .userId(userId)
//            .deviceId(deviceId)
//            .streamId(streamId)
//            .status(status)
//            .tillTheEnd(tillTheEnd)
//            .build();
//    repository.save(streamControlData);
//
//    StreamControlData userRunning = repository.isUserRunning(userId);
//    StreamControlData deviceRunning = repository.isDeviceRunning(deviceId);
//    assertThat(userRunning).isEqualTo(deviceRunning);
//    return streamControlData;
//  }
//
//  @Test
//  public void given_NoStreamingRunning_when_PlayANewStream_then_AllShouldWork() throws JsonProcessingException {
//
//    final long userId = 1;
//    final long streamId = 1;
//    final long deviceId = 1;
//
//    final String uri = String.format("/dynamic-data/user-device/device/%d",deviceId);
//
//
//    InputUserDevice userDevice = InputUserDevice.builder()
//            .userId(String.valueOf(userId))
//            .devices(Set.of(String.valueOf(deviceId)))
//            .build();
//
//    stubFor(get(urlEqualTo(uri))
//            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
//            .willReturn(aResponse()
//                    .withStatus(HttpStatus.OK.value())
//                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                    .withBody(JsonUtils.ObjectToJson(userDevice))));
//
//
//    StreamControlReturnValue returnValue = view.play(streamId, deviceId);
//
//    assertThat(returnValue).isInstanceOf(StreamControlReturnValueOk.class);
//    StreamControlReturnValueOk returnValueOk = ((StreamControlReturnValueOk) returnValue);
//
//    assertThat(returnValueOk.getUserId()).isEqualTo(String.valueOf(userId));
//    assertThat(returnValueOk.getDeviceId()).isEqualTo(String.valueOf(deviceId));
//    assertThat(returnValueOk.getStreamId()).isEqualTo(String.valueOf(streamId));
//  }
//
//  @Test
//  public void given_AStreamingRunning_when_PlayANewStream_then_AllShould_NOT_Work() throws JsonProcessingException {
//
//    final long userId = 1;
//    final long streamId = 1;
//    final long anotherStreamId = 2;
//    final long deviceId = 1;
//    final String uri = String.format("/dynamic-data/user-device/device/%d",deviceId);
//
//    InputUserDevice userDevice = InputUserDevice.builder()
//            .userId(String.valueOf(userId))
//            .devices(Set.of(String.valueOf(deviceId)))
//            .build();
//
//    stubFor(get(urlEqualTo(uri))
//            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
//            .willReturn(aResponse()
//                    .withStatus(HttpStatus.OK.value())
//                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                    .withBody(JsonUtils.ObjectToJson(userDevice))));
//
//    StreamControlData alreadyRunning = addCheckedStreamControlData(userId,
//            deviceId,
//            anotherStreamId,
//            StreamStatus.RUNNING,
//            false);
//
//
//    StreamControlReturnValue returnValue = view.play(streamId, deviceId);
//    LOG.error("returnValue: " + returnValue);
//
//    assertThat(returnValue).isInstanceOf(StreamControlReturnValueError.class);
//    StreamControlReturnValueError invalidResponse = (StreamControlReturnValueError) returnValue;
//    assertThat(invalidResponse.getMessage()).startsWith("Invalid");
//    assertThat(invalidResponse.getErrorCode()).isEqualTo(StreamControlReturnValueError.ErrorCode.INVALID_PARAMETER);
//
//  }
//
//  @Test
//  public void given_AStreamingRunning_when_StopAExistingStream_then_AllShouldWork() {
//
//    final long userId = 1;
//    final long streamId = 1;
//    final long anotherStreamId = 1;
//    final long deviceId = 1;
//
//    StreamControlData alreadyRunning = addCheckedStreamControlData(userId,
//            deviceId,
//            anotherStreamId,
//            StreamStatus.RUNNING,
//            false);
//
//    StreamControlReturnValue returnValue = view.stop(deviceId);
//
//    assertThat(returnValue).isInstanceOf(StreamControlReturnValueOk.class);
//    StreamControlReturnValueOk returnValueOk = (StreamControlReturnValueOk) returnValue;
//
//    assertThat(returnValueOk.getUserId()).isEqualTo(String.valueOf(userId));
//    assertThat(returnValueOk.getDeviceId()).isEqualTo(String.valueOf(deviceId));
//    assertThat(returnValueOk.getStreamId()).isEqualTo(String.valueOf(streamId));
//
//  }
//
//  @Test
//  public void given_NoStreamingRunning_when_StopANonExistingStream_then_AllShould_NOT_Work() throws JsonProcessingException {
//
//    final long deviceId = 1;
//
//    StreamControlReturnValue returnValue = view.stop(deviceId);
//    LOG.error("returnValue: " + returnValue);
//
//    assertThat(returnValue).isInstanceOf(StreamControlReturnValueError.class);
//    StreamControlReturnValueError invalidResponse = (StreamControlReturnValueError) returnValue;
//    assertThat(invalidResponse.getMessage()).startsWith("Invalid");
//    assertThat(invalidResponse.getErrorCode()).isEqualTo(StreamControlReturnValueError.ErrorCode.INVALID_PARAMETER);
//  }
//
//  @Test
//  public void given_AStreamingRunning_when_PauseAExistingStream_then_AllShouldWork() {
//
//    final long userId = 1;
//    final long deviceId = 1;
//    final long streamId = 1;
//
//    StreamControlData alreadyRunning = addCheckedStreamControlData(userId,
//            deviceId,
//            streamId,
//            StreamStatus.RUNNING,
//            false);
//
//    StreamControlReturnValue returnValue = view.pause(deviceId);
//
//    assertThat(returnValue).isInstanceOf(StreamControlReturnValueOk.class);
//    StreamControlReturnValueOk returnValueOk = (StreamControlReturnValueOk) returnValue;
//
//    assertThat(returnValueOk.getUserId()).isEqualTo(String.valueOf(userId));
//    assertThat(returnValueOk.getDeviceId()).isEqualTo(String.valueOf(deviceId));
//    assertThat(returnValueOk.getStreamId()).isEqualTo(String.valueOf(streamId));
//
//  }
//
//  @Test
//  public void given_NoStreamingRunning_when_PauseANonExistingStream_then_AllShould_NOT_Work() throws JsonProcessingException {
//
//    final long deviceId = 1;
//
//    StreamControlReturnValue returnValue = view.pause(deviceId);
//
//    assertThat(returnValue).isInstanceOf(StreamControlReturnValueError.class);
//    StreamControlReturnValueError invalidResponse = (StreamControlReturnValueError) returnValue;
//    assertThat(invalidResponse.getMessage()).startsWith("Invalid");
//    assertThat(invalidResponse.getErrorCode()).isEqualTo(StreamControlReturnValueError.ErrorCode.INVALID_PARAMETER);
//  }
}

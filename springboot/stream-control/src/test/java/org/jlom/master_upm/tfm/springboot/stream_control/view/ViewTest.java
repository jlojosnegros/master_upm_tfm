package org.jlom.master_upm.tfm.springboot.stream_control.view;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.StreamControlService;
import org.jlom.master_upm.tfm.springboot.stream_control.model.daos.StreamControlData;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.fail;
import static org.jlom.master_upm.tfm.springboot.stream_control.utils.JsonUtils.ObjectToJson;



@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ViewTest {

  @LocalServerPort
  private int port;

  @Autowired
  private StreamControlService service;

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

  private void addCheckedStreamControlData(long userId, Set<Long> deviceIds) {

//    StreamControlData streamControlData = StreamControlData.builder()
//            .userId(userId)
//            .devices(deviceIds)
//            .build();
//
//    StreamControlServiceResponse response = service.createUser(streamControlData.getUserId(),
//            streamControlData.getDevices());
//    assertThat(response).isInstanceOf(StreamControlServiceResponseOK.class);
//
//    StreamControlData actualStreamControl = ((StreamControlServiceResponseOK) response).getStreamControl();
//    assertThat(actualStreamControl).isEqualTo(streamControlData);
    fail("Not implemented");

  }

  private Map<String,StreamControlData> addSomeCheckedStreamControls(final int numberOfElements) {
    Map<String,StreamControlData> users = new HashMap<>(numberOfElements);

//    for(int idx = 0; idx <= numberOfElements; idx++) {
//
//      long userId = idx+1;
//      Set<Long> devices = LongStream.rangeClosed((idx * numberOfElements) + 1, ((idx + 1) * numberOfElements))
//              .boxed()
//              .collect(Collectors.toSet());
//
//      users.putIfAbsent(userId,new StreamControlData(userId, devices));
//      addCheckedStreamControlData(userId,devices);
//    }

    return users;
  }
}

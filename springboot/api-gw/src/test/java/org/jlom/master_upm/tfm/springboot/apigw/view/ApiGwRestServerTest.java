package org.jlom.master_upm.tfm.springboot.apigw.view;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.jlom.master_upm.tfm.springboot.apigw.utils.JsonUtils.ObjectToJson;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiGwRestServerTest {

  private static final Logger LOG = LoggerFactory.getLogger(ApiGwRestServerTest.class);


  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;


  private HttpResponse getRestResponseTo(String resourceUri) throws IOException {
    HttpClient client = HttpClientBuilder.create().build();
    return client.execute(new HttpGet("http://localhost:"+port+resourceUri));
  }


  @NotNull
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

  @Test
  public void dummy() {}
}

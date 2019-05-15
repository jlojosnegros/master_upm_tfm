package org.jlom.master_upm.tfm.springboot.catalog.view;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.jlom.master_upm.tfm.springboot.catalog.controller.CatalogService;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContentRepository;
import org.jlom.master_upm.tfm.springboot.catalog.model.ContentStatus;
import org.jlom.master_upm.tfm.springboot.catalog.view.api.dtos.InputCatalogContent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import static org.jlom.master_upm.tfm.springboot.catalog.utils.DtosTransformations.serviceToViewContent;
import static org.jlom.master_upm.tfm.springboot.catalog.utils.JsonUtils.ObjectToJson;
import static org.jlom.master_upm.tfm.springboot.catalog.utils.JsonUtils.retrieveListOfResourcesFromResponse;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CompleteTests {

  @Autowired
  private CatalogContentRepository repository;

  @Autowired
  private CatalogService service;

  @LocalServerPort
  private int port;

  private static final Logger LOG = LoggerFactory.getLogger(CompleteTests.class);

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
  public void find_SOON() throws IOException {
    //given

    final Date now = Date.from(Instant.now());


    CatalogContent expectedContentOne = CatalogContent.builder()
            .contentId(1)
            .status(ContentStatus.AVAILABLE)
            .title("uno")
            .streamId(1)
            .available(now)
            .tags(Set.of("tag1", "tag2"))
            .build();
    repository.save(expectedContentOne);

    CatalogContent expectedContentTwo = CatalogContent.builder()
            .contentId(2)
            .status(ContentStatus.SOON)
            .title("dos")
            .streamId(2)
            .available(now)
            .tags(Set.of("tag1", "tag2"))
            .build();
    repository.save(expectedContentTwo);

    List<CatalogContent> expectedContents = List.of(expectedContentOne, expectedContentTwo);


    //when
    HttpResponse response = getRestResponseTo("/catalog/content/status/SOON");


    //then
    int statusCode = response.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(200);

    List<InputCatalogContent> catalogContents = retrieveListOfResourcesFromResponse(response, InputCatalogContent.class);

    LOG.debug("sent    :" + expectedContents);
    LOG.debug("received: " + catalogContents);

    Assertions.assertThat(catalogContents).containsExactly(serviceToViewContent(expectedContentTwo));

  }
  @Test
  public void when_AskingForContentWithSpecificDate_then_OnlyThoseAvailableAfterThatDateAreReturned() throws IOException {
    //given

    Date now = Date.from(Instant.now());

    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(now);
    cal.add(Calendar.DATE,-15); // add one day
    Date halfMonthAgo = cal.getTime();

    cal.setTime(now);
    cal.add(Calendar.DATE, -30);
    Date aMonthAgo = cal.getTime();

    CatalogContent expectedContentOne = CatalogContent.builder()
            .contentId(1)
            .status(ContentStatus.AVAILABLE)
            .title("uno")
            .streamId(1)
            .available(now)
            .tags(Set.of("tag1", "tag2"))
            .build();
    repository.save(expectedContentOne);

    CatalogContent expectedContentTwo = CatalogContent.builder()
            .contentId(2)
            .status(ContentStatus.SOON)
            .title("dos")
            .streamId(2)
            .available(aMonthAgo)
            .tags(Set.of("tag1", "tag2"))
            .build();
    repository.save(expectedContentTwo);

    List<CatalogContent> expectedContents = List.of(expectedContentOne, expectedContentTwo);

    //when
    HttpResponse response = getRestResponseTo("/catalog/content/after?date=" + halfMonthAgo.getTime());


    //then
    int statusCode = response.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(200);

    List<InputCatalogContent> catalogContents = retrieveListOfResourcesFromResponse(response, InputCatalogContent.class);

    LOG.debug("sent    :" + expectedContents);
    LOG.debug("received: " + catalogContents);

    Assertions.assertThat(catalogContents).containsOnly(serviceToViewContent(expectedContentOne));

  }
}

package org.jlom.master_upm.tfm.springboot.catalog.view;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.springboot.catalog.controller.CatalogService;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.springboot.catalog.model.ContentStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import static org.jlom.master_upm.tfm.springboot.catalog.utils.JsonUtils.retrieveListOfResourcesFromResponse;
import static org.jlom.master_upm.tfm.springboot.catalog.utils.JsonUtils.retrieveResourceFromResponse;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CatalogRestServerTest {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogRestServerTest.class);

  @MockBean
  private CatalogService service;

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;


  private HttpResponse getRestResponseTo(String resourceUri) throws IOException {
    HttpClient client = HttpClientBuilder.create().build();
    return client.execute(new HttpGet("http://localhost:"+port+resourceUri));
  }

  @Test
  public void given_TwoElementsInDB_when_RequestToListAllContents_then_OnlyThoseTwoElementsAreListed() throws IOException {
    //given
    CatalogContent expectedContentOne = CatalogContent.builder()
            .contentId(1)
            .status(ContentStatus.AVAILABLE)
            .title("uno")
            .streamId(1)
            .available(Date.from(Instant.now()))
            .tags(Set.of("tag1", "tag2"))
            .build();
    CatalogContent expectedContentTwo = CatalogContent.builder()
            .contentId(2)
            .status(ContentStatus.SOON)
            .title("dos")
            .streamId(2)
            .available(Date.from(Instant.now()))
            .tags(Set.of("tag1", "tag3"))
            .build();

    List<CatalogContent> expectedContents = List.of(expectedContentOne, expectedContentTwo);

    Mockito.doReturn(expectedContentOne).when(service).getContent(1);

    //when
    HttpResponse response = getRestResponseTo("/catalog/content/1");

    //then
    int statusCode = response.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(200);

    CatalogContent catalogContent = retrieveResourceFromResponse(response, CatalogContent.class);

    LOG.debug("sent    :" + expectedContentOne);
    LOG.debug("received: " + catalogContent);

    Assertions.assertThat(catalogContent).isEqualTo(expectedContentOne);

  }

  @Test
  public void when_AskingForContentWithSpecificTag_then_OnlyThoseWithTheTagAreReturned() throws IOException {
    //given
    CatalogContent expectedContentOne = CatalogContent.builder()
            .contentId(1)
            .status(ContentStatus.AVAILABLE)
            .title("uno")
            .streamId(1)
            .available(Date.from(Instant.now()))
            .tags(Set.of("tag1", "tag2"))
            .build();
    CatalogContent expectedContentTwo = CatalogContent.builder()
            .contentId(2)
            .status(ContentStatus.SOON)
            .title("dos")
            .streamId(2)
            .available(Date.from(Instant.now()))
            .tags(Set.of("tag1", "tag2"))
            .build();

    List<CatalogContent> expectedContents = List.of(expectedContentOne, expectedContentTwo);

    Mockito.doReturn(expectedContents).when(service)
            .getContentsWithTags(Set.of("tag1","tag2"));


    //when
    HttpResponse response = getRestResponseTo("/catalog/content/exactlyWithTags?tags=tag1,tag2");


    //then
    int statusCode = response.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(200);

    List<CatalogContent> catalogContents = retrieveListOfResourcesFromResponse(response, CatalogContent.class);

    LOG.debug("sent    :" + expectedContents);
    LOG.debug("received: " + catalogContents);

    Assertions.assertThat(catalogContents).containsOnly(expectedContentOne,expectedContentTwo);

  }

  @Test
  public void when_AskingForContentWithSpecificDate_then_OnlyThoseWithTheTagAreReturned() throws IOException {
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

    CatalogContent expectedContentTwo = CatalogContent.builder()
            .contentId(2)
            .status(ContentStatus.SOON)
            .title("dos")
            .streamId(2)
            .available(halfMonthAgo)
            .tags(Set.of("tag1", "tag2"))
            .build();

    List<CatalogContent> expectedContents = List.of(expectedContentOne, expectedContentTwo);

    Mockito.doReturn(List.of(expectedContentOne)).when(service)
            .getAvailableAfter(aMonthAgo);

    //when
    HttpResponse response = getRestResponseTo("/catalog/content/after?date=" + aMonthAgo.getTime());


    //then
    int statusCode = response.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(200);

    List<CatalogContent> catalogContents = retrieveListOfResourcesFromResponse(response, CatalogContent.class);

    LOG.debug("sent    :" + expectedContents);
    LOG.debug("received: " + catalogContents);

    Assertions.assertThat(catalogContents).containsOnly(expectedContentOne);

  }

}

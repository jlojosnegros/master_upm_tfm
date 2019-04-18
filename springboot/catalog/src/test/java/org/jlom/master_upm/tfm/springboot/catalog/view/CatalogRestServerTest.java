package org.jlom.master_upm.tfm.springboot.catalog.view;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.springboot.catalog.controller.CatalogService;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.springboot.catalog.model.ContentStatus;
import org.jlom.master_upm.tfm.springboot.catalog.view.api.dtos.ProblemDetails;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
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
  int port;

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
  public void when_AskingForContentWithExpecificTag_then_OnlyThoseWithTheTagAreReturned() throws IOException {
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
    HttpResponse response = getRestResponseTo("/catalog/content");


    //then
    int statusCode = response.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(200);

    List<CatalogContent> catalogContents = retrieveListOfResourcesFromResponse(response, CatalogContent.class);

    LOG.debug("sent    :" + expectedContents);
    LOG.debug("received: " + catalogContents);

    Assertions.assertThat(catalogContents).containsOnly(expectedContentOne,expectedContentTwo);

  }

}

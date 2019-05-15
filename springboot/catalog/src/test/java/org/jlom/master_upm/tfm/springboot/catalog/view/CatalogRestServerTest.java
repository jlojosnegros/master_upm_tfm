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
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseOk;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContentRepository;
import org.jlom.master_upm.tfm.springboot.catalog.model.ContentStatus;
import org.jlom.master_upm.tfm.springboot.catalog.view.api.dtos.InputCatalogContent;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jlom.master_upm.tfm.springboot.catalog.utils.DtosTransformations.serviceToViewContent;
import static org.jlom.master_upm.tfm.springboot.catalog.utils.JsonUtils.ObjectToJson;
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
  public void given_TwoElementsInDB_when_AskingForContentWithId_then_OnlyOneIsReturned() throws IOException {
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

    InputCatalogContent catalogContent = retrieveResourceFromResponse(response, InputCatalogContent.class);

    LOG.debug("sent    :" + expectedContentOne);
    LOG.debug("received: " + catalogContent);

    Assertions.assertThat(catalogContent).isEqualTo(serviceToViewContent(expectedContentOne));

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

    List<InputCatalogContent> catalogContents = retrieveListOfResourcesFromResponse(response, InputCatalogContent.class);

    LOG.debug("sent    :" + expectedContents);
    LOG.debug("received: " + catalogContents);

    Assertions.assertThat(catalogContents).containsOnly(serviceToViewContent(expectedContentOne),
            serviceToViewContent(expectedContentTwo));

  }

  @Test
  public void when_AskingForAllContents_then_AllExistingContentsAreReturned() throws IOException {
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

    Mockito.doReturn(expectedContents).when(service).listAll();


    //when
    HttpResponse response = getRestResponseTo("/catalog/content");


    //then
    int statusCode = response.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(200);

    List<InputCatalogContent> catalogContents = retrieveListOfResourcesFromResponse(response,
            InputCatalogContent.class);

    LOG.debug("sent    :" + expectedContents);
    LOG.debug("received: " + catalogContents);

    Assertions.assertThat(catalogContents).containsOnly(serviceToViewContent(expectedContentOne),serviceToViewContent(expectedContentTwo));

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

    CatalogContent expectedContentTwo = CatalogContent.builder()
            .contentId(2)
            .status(ContentStatus.SOON)
            .title("dos")
            .streamId(2)
            .available(aMonthAgo)
            .tags(Set.of("tag1", "tag2"))
            .build();

    List<CatalogContent> expectedContents = List.of(expectedContentOne, expectedContentTwo);

    Mockito.doReturn(List.of(expectedContentOne)).when(service)
            .getAvailableAfter(aMonthAgo);

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

  @Test
  public void when_AskingForContentWithSpecificStreamId_then_OnlyOneIsReturned() throws IOException {
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

    CatalogContent expectedContentTwo = CatalogContent.builder()
            .contentId(2)
            .status(ContentStatus.SOON)
            .title("dos")
            .streamId(2)
            .available(now)
            .tags(Set.of("tag1", "tag2"))
            .build();

    List<CatalogContent> expectedContents = List.of(expectedContentOne, expectedContentTwo);

    Mockito.doReturn(expectedContentOne).when(service)
            .getContentWithStream(1);
    Mockito.doReturn(expectedContentTwo).when(service)
            .getContentWithStream(2);

    //when
    HttpResponse response = getRestResponseTo("/catalog/content/stream/1");


    //then
    int statusCode = response.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(200);

    InputCatalogContent catalogContents = retrieveResourceFromResponse(response, InputCatalogContent.class);

    LOG.debug("sent    :" + expectedContents);
    LOG.debug("received: " + catalogContents);

    Assertions.assertThat(catalogContents).isEqualTo(serviceToViewContent(expectedContentOne));

  }

  @Test
  public void when_createNewContent_allIsOk() throws IOException {
    //given


    final long contentId = 1;
    final long streamId = 1;
    final Date available = Date.from(Instant.now());
    final ContentStatus status = ContentStatus.AVAILABLE;
    final String title = "uno";
    final Set<String> tags = Set.of("tag1", "tag2");


    var inputContent = InputCatalogContent.builder()
            .title(title)
            .contentStatus(status.name())
            .streamId(String.valueOf(streamId))
            .available(available)
            .tags(tags)
            .build();

    CatalogContent expectedContentOne = CatalogContent.builder()
            .title(title)
            .status(status)
            .streamId(streamId)
            .available(available)
            .tags(tags)
            .contentId(contentId)
            .build();

    Mockito.doReturn(new ContentServiceResponseOk(expectedContentOne))
            .when(service)
            .createContent(streamId,title, status, tags);


    //when
    HttpResponse response = postMessageTo("/catalog/content/newContent/", inputContent);


    //then
    int statusCode = response.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.CREATED.value());

    InputCatalogContent catalogContents = retrieveResourceFromResponse(response, InputCatalogContent.class);

    LOG.debug("sent    :" + inputContent);
    LOG.debug("received: " + catalogContents);

    Assertions.assertThat(catalogContents).isEqualTo(serviceToViewContent(expectedContentOne));

  }

  @Test
  public void when_deleteAnExistingContent_allIsOk() throws IOException {
    //given


    final long contentId = 1;
    final long streamId = 1;
    final Date available = Date.from(Instant.now());
    final ContentStatus status = ContentStatus.AVAILABLE;
    final String title = "uno";
    final Set<String> tags = Set.of("tag1", "tag2");


    var inputContent = InputCatalogContent.builder()
            .title(title)
            .contentStatus(status.name())
            .streamId(String.valueOf(streamId))
            .available(available)
            .tags(tags)
            .build();

    CatalogContent expectedContentOne = CatalogContent.builder()
            .title(title)
            .status(status)
            .streamId(streamId)
            .available(available)
            .tags(tags)
            .contentId(contentId)
            .build();

    Mockito.doReturn(new ContentServiceResponseOk(expectedContentOne))
            .when(service)
            .deleteContent(contentId);


    //when
    HttpResponse response = postMessageTo("/catalog/content/delete/" + contentId);


    //then
    int statusCode = response.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());

    InputCatalogContent catalogContents = retrieveResourceFromResponse(response, InputCatalogContent.class);

    LOG.debug("sent    :" + inputContent);
    LOG.debug("received: " + catalogContents);

    Assertions.assertThat(catalogContents).isEqualTo(serviceToViewContent(expectedContentOne));

  }

  @Test
  public void when_changeStatus_allIsOk() throws IOException {
    //given


    final long contentId = 1;
    final long streamId = 1;
    final Date available = Date.from(Instant.now());
    final ContentStatus status = ContentStatus.SOON;
    final ContentStatus newStatus = ContentStatus.AVAILABLE;
    final String title = "uno";
    final Set<String> tags = Set.of("tag1", "tag2");


    var inputContent = InputCatalogContent.builder()
            .title(title)
            .contentStatus(status.name())
            .streamId(String.valueOf(streamId))
            .available(available)
            .tags(tags)
            .build();

    CatalogContent expectedContentOne = CatalogContent.builder()
            .title(title)
            .status(newStatus)
            .streamId(streamId)
            .available(available)
            .tags(tags)
            .contentId(contentId)
            .build();

    Mockito.doReturn(new ContentServiceResponseOk(expectedContentOne))
            .when(service)
            .changeStatus(contentId,newStatus);


    //when
    HttpResponse response = postMessageTo("/catalog/content/changeStatus/" + contentId, newStatus.name());


    //then
    int statusCode = response.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());

    InputCatalogContent catalogContents = retrieveResourceFromResponse(response, InputCatalogContent.class);

    LOG.debug("sent    :" + inputContent);
    LOG.debug("received: " + catalogContents);

    Assertions.assertThat(catalogContents).isEqualTo(serviceToViewContent(expectedContentOne));

  }

  @Test
  public void when_addTags_allIsOk() throws IOException {
    //given
    final long contentId = 1;
    final long streamId = 1;
    final Date available = Date.from(Instant.now());
    final ContentStatus status = ContentStatus.AVAILABLE;
    final String title = "uno";
    final Set<String> tags = Set.of("tag1", "tag2");
    final Set<String> newtags = Set.of("tag3", "tag2");
    final Set<String> expectedTags = Set.of("tag1","tag3", "tag2");


    var inputContent = InputCatalogContent.builder()
            .title(title)
            .contentStatus(status.name())
            .streamId(String.valueOf(streamId))
            .available(available)
            .tags(tags)
            .build();

    CatalogContent expectedContentOne = CatalogContent.builder()
            .title(title)
            .status(status)
            .streamId(streamId)
            .available(available)
            .tags(expectedTags)
            .contentId(contentId)
            .build();

    Mockito.doReturn(new ContentServiceResponseOk(expectedContentOne))
            .when(service)
            .addTags(contentId,newtags);



    //when
    HttpResponse response = postMessageTo("/catalog/content/addTags/" + contentId
            +"?newTags="+ String.join(",", newtags));


    //then
    int statusCode = response.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());

    InputCatalogContent catalogContents = retrieveResourceFromResponse(response, InputCatalogContent.class);

    LOG.debug("sent    :" + inputContent);
    LOG.debug("received: " + catalogContents);

    Assertions.assertThat(catalogContents).isEqualTo(serviceToViewContent(expectedContentOne));

  }
}

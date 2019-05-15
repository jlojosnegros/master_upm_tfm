package org.jlom.master_upm.tfm.micronaut.catalog.view;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.micronaut.catalog.controller.CatalogContentService;
import org.jlom.master_upm.tfm.micronaut.catalog.controller.api.dtos.ContentServiceResponseOk;
import org.jlom.master_upm.tfm.micronaut.catalog.controller.api.dtos.ICatalogService;
import org.jlom.master_upm.tfm.micronaut.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.micronaut.catalog.model.CatalogContentRepository;
import org.jlom.master_upm.tfm.micronaut.catalog.model.ContentStatus;
import org.jlom.master_upm.tfm.micronaut.catalog.view.api.dtos.InputCatalogContent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jlom.master_upm.tfm.micronaut.catalog.utils.DtosTransformations.serviceToViewContent;
import static org.jlom.master_upm.tfm.micronaut.catalog.utils.JsonUtils.jsonToList;
import static org.jlom.master_upm.tfm.micronaut.catalog.utils.JsonUtils.jsonToObject;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@MicronautTest
public class ViewTest {

  private static final Logger LOG = LoggerFactory.getLogger(ViewTest.class);

  @Inject
  @Client("/")
  private HttpClient client;

  @Inject
  private CatalogContentRepository repository;


  @Inject
  private ICatalogService service;

  @MockBean(CatalogContentService.class)
  public ICatalogService mockService() {
    return Mockito.spy(new CatalogContentService(repository));
  }



  @Test
  public void when_AskingForAllContents_then_AllExistingContentsAreReturned() throws Exception {

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


    String body = client.toBlocking().retrieve("/catalog/content");
    assertNotNull(body);

    var inputCatalogContents = jsonToList(body, InputCatalogContent.class);

    assertThat(inputCatalogContents)
            .containsExactlyInAnyOrder(serviceToViewContent(expectedContents)
                    .toArray(new InputCatalogContent[0]));
  }

  @Test
  public void given_TwoElementsInDB_when_AskingForContentWithId_then_OnlyOneIsReturned() {

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

    when(service.getContent(1)).thenReturn(expectedContentOne);

    InputCatalogContent catalogContent = client.toBlocking()
            .retrieve(HttpRequest.GET("/catalog/content/1"), InputCatalogContent.class);

    LOG.error("sent    :" + expectedContentOne);
    LOG.error("received: " + catalogContent);

    assertThat(catalogContent).isEqualTo(serviceToViewContent(expectedContentOne));

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
    repository.save(expectedContentOne);

    CatalogContent expectedContentTwo = CatalogContent.builder()
            .contentId(2)
            .status(ContentStatus.SOON)
            .title("dos")
            .streamId(2)
            .available(Date.from(Instant.now()))
            .tags(Set.of("tag1", "tag2"))
            .build();
    repository.save(expectedContentTwo);

    CatalogContent expectedContentThree = CatalogContent.builder()
            .contentId(3)
            .status(ContentStatus.SOON)
            .title("tres")
            .streamId(3)
            .available(Date.from(Instant.now()))
            .tags(Set.of("tag1", "tag3"))
            .build();
    repository.save(expectedContentThree);

    List<CatalogContent> expectedContents = List.of(expectedContentOne, expectedContentTwo);



    //when
    String body = client.toBlocking()
            .retrieve(HttpRequest.GET("/catalog/content/exactlyWithTags?tags=tag1,tag2"));
    assertThat(body).isNotEmpty();

    //then
    List<InputCatalogContent> catalogContents = jsonToList(body, InputCatalogContent.class);
    List<CatalogContent> withExactlyTags = repository.findWithExactlyTags(Set.of("tag1", "tag2"));

    LOG.info("sent    :" + expectedContents);
    LOG.info("received: " + catalogContents);
    //LOG.info("in_repo: " + withExactlyTags);

    assertThat(catalogContents).containsOnly(serviceToViewContent(expectedContentOne),
            serviceToViewContent(expectedContentTwo));

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
            .getAvailableAfter(halfMonthAgo);

    //when
    String body = client.toBlocking()
            .retrieve(HttpRequest.GET("/catalog/content/after?date=" + halfMonthAgo.getTime()));

    //then

    List<InputCatalogContent> catalogContents = jsonToList(body, InputCatalogContent.class);

    LOG.debug("sent    :" + expectedContents);
    LOG.debug("received: " + catalogContents);

    assertThat(catalogContents).containsOnly(serviceToViewContent(expectedContentOne));

  }

  @Test
  public void FULL_when_AskingForContentWithSpecificDate_then_OnlyThoseAvailableAfterThatDateAreReturned() throws IOException {
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
    String body = client.toBlocking()
            .retrieve(HttpRequest.GET("/catalog/content/after?date=" + halfMonthAgo.getTime()));

    //then

    List<InputCatalogContent> catalogContents = jsonToList(body, InputCatalogContent.class);

    LOG.debug("sent    :" + expectedContents);
    LOG.debug("received: " + catalogContents);

    assertThat(catalogContents).containsOnly(serviceToViewContent(expectedContentOne));

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
    String body = client.toBlocking()
            .retrieve(HttpRequest.GET("/catalog/content/stream/1"));
    LOG.info("response body:" + body);
    //then
    InputCatalogContent catalogContents = jsonToObject(body, InputCatalogContent.class);

    LOG.info("sent    :" + expectedContents);
    LOG.info("received: " + catalogContents);

    assertThat(catalogContents).isEqualTo(serviceToViewContent(expectedContentOne));

  }

  @Test
  public void FULL_when_AskingForContentWithSpecificStreamId_then_OnlyOneIsReturned() throws IOException {
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
    String body = client.toBlocking()
            .retrieve(HttpRequest.GET("/catalog/content/stream/1"));
    LOG.info("response body:" + body);
    //then
    InputCatalogContent catalogContents = jsonToObject(body, InputCatalogContent.class);

    LOG.info("sent    :" + expectedContents);
    LOG.info("received: " + catalogContents);

    assertThat(catalogContents).isEqualTo(serviceToViewContent(expectedContentOne));

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
    String body = client.toBlocking()
            .retrieve(HttpRequest.POST("/catalog/content/newContent/", inputContent));

    //then
    InputCatalogContent catalogContents = jsonToObject(body, InputCatalogContent.class);

    LOG.debug("sent    :" + inputContent);
    LOG.debug("received: " + catalogContents);

    Assertions.assertThat(catalogContents).isEqualTo(serviceToViewContent(expectedContentOne));

  }
}
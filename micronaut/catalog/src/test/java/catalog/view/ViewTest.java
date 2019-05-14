package catalog.view;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.micronaut.catalog.controller.CatalogContentService;
import org.jlom.master_upm.tfm.micronaut.catalog.controller.api.CatalogServiceQueries;
import org.jlom.master_upm.tfm.micronaut.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.micronaut.catalog.model.CatalogContentRepository;
import org.jlom.master_upm.tfm.micronaut.catalog.model.ContentStatus;
import org.jlom.master_upm.tfm.micronaut.catalog.utils.JsonUtils;
import org.jlom.master_upm.tfm.micronaut.catalog.view.api.dtos.InputCatalogContent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jlom.master_upm.tfm.micronaut.catalog.utils.DtosTransformations.serviceToViewContent;
import static org.jlom.master_upm.tfm.micronaut.catalog.utils.JsonUtils.jsonToList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;



@MicronautTest
public class ViewTest {

  private static final Logger LOG = LoggerFactory.getLogger(ViewTest.class);

  private static EmbeddedServer server;
  private static HttpClient client;

  @Inject
  private CatalogContentRepository repository;


  @Inject
  private CatalogContentService service;


//  @MockBean
//  @Primary
//  public CatalogServiceQueries mockService() {
//    return Mockito.mock(CatalogServiceQueries.class);
//  }


  @BeforeAll
  public static void setupServer() {
    server = ApplicationContext.run(EmbeddedServer.class);

    client = server
            .getApplicationContext()
            .createBean(HttpClient.class, server.getURL());
  }

  @AfterAll
  public static void stopServer() {
    if (server != null) {
      server.stop();
    }
    if (client != null) {
      client.stop();
    }
  }

  @Test
  public void testFindAll() throws Exception {

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

    //when(service.getContent(1)).thenReturn(expectedContentOne);

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
}
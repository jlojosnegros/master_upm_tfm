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
import org.jlom.master_upm.tfm.micronaut.catalog.view.api.dtos.InputCatalogContent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    Assertions.assertThat(inputCatalogContents)
            .containsExactlyInAnyOrder(serviceToViewContent(expectedContents)
                    .toArray(new InputCatalogContent[0]));
  }

  @Test
  public void testService() {

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

    //when(service.getContent(1)).thenReturn(expectedContentOne);

    InputCatalogContent catalogContent = client.toBlocking()
            .retrieve(HttpRequest.GET("/catalog/content/1"), InputCatalogContent.class);

    LOG.error("sent    :" + expectedContentOne);
    LOG.error("received: " + catalogContent);

    Assertions.assertThat(catalogContent).isEqualTo(serviceToViewContent(expectedContentOne));

  }
}
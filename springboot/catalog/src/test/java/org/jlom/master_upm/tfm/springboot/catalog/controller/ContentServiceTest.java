package org.jlom.master_upm.tfm.springboot.catalog.controller;

import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponse;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseFailure;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseOk;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContentRepository;
import org.jlom.master_upm.tfm.springboot.catalog.model.ContentStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.text.AbstractDocument;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ContentServiceTest {

  @MockBean
  private CatalogContentRepository repository;

  @Autowired
  private CatalogService service;


  @Test
  public void given_ANewContent_when_TryToCreateANewContent_then_everythingWorks() {

    final long expectedContentId = 1;
    final long expectedStreamId = 1;
    final String expectedTitle = "title";
    final ContentStatus status = ContentStatus.AVAILABLE;
    Set<String> expectedTags = Set.of("tag1", "tag2");

    Mockito.doNothing()
            .when(repository)
            .save(Mockito.any(CatalogContent.class));

    CatalogContent expectedContent = CatalogContent.builder()
            .contentId(expectedContentId)
            .status(status)
            .title(expectedTitle)
            .tags(expectedTags)
            .build();

    Mockito.when(repository.findByStreamId(expectedStreamId))
            .thenReturn(null)
            .thenReturn(expectedContent)
            .thenReturn(null);

    ContentServiceResponse response = service.createContent(expectedStreamId,
            expectedTitle,
            status,
            expectedTags);

    Assertions.assertThat(response).isInstanceOf(ContentServiceResponseOk.class);

  }

  @Test
  public void given_ANewContent_when_TryToCreateANewContent_then_somethingFails() {

    Mockito.doThrow(new RuntimeException("error: Id already exist"))
            .when(repository)
            .save(Mockito.any(CatalogContent.class));

    final long expectedContentId = 1;
    final long expectedStreamId = 1;
    final String expectedTitle = "title";
    final ContentStatus status = ContentStatus.SOON;

    Set<String> expectedTags = Set.of("tag1", "tag2");

    ContentServiceResponse response = service.createContent(expectedStreamId,
            expectedTitle,
            status,
            expectedTags);

    Assertions.assertThat(response).isInstanceOf(ContentServiceResponseFailure.class);

  }

  @Test
  public void when_TryToCreateAnAlreadyExistingContent_then_ShouldFail() {

    final long expectedContentId = 1;
    final long expectedStreamId = 1;
    final String expectedTitle = "title";
    final ContentStatus status = ContentStatus.ON_HOLD;

    String[] expectedTags = new String[]{"tag1", "tag2"};

    //given
    Mockito.doNothing()
            .when(repository)
            .save(Mockito.any(CatalogContent.class));

    Mockito.doReturn(CatalogContent.builder()
            .contentId(expectedContentId)
            .streamId(expectedStreamId)
            .status(status)
            .title(expectedTitle)
            .tags(Set.of(expectedTags))
            .build()
    ).when(repository).findById(expectedContentId);


    ContentServiceResponse response = service.createContent(expectedStreamId,
            expectedTitle, status,
            Set.of(expectedTags));

    Assertions.assertThat(response).isInstanceOf(ContentServiceResponseFailure.class);

  }
}

package org.jlom.master_upm.tfm.springboot.catalog.controller;

import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponse;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseFailure;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseOk;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ContentServiceTest {

  @MockBean
  private CatalogContentRepository repository;

  @Autowired
  private CatalogService service;


  @Test
  public void given_ANewContent_when_TrytoCreateANewContent_then_everythingWorks() {

    Mockito.doNothing()
            .when(repository)
            .save(Mockito.any(CatalogContent.class));

    long expectedContentId = 1;
    long expectedStreamId = 1;
    String expectedTitle = "title";
    Set<String> expectedTags = Set.of("tag1", "tag2");

    ContentServiceResponse response = service.createContent(expectedContentId,
            expectedStreamId,
            expectedTitle,
            expectedTags);

    Assertions.assertThat(response).isInstanceOf(ContentServiceResponseOk.class);

  }

  @Test
  public void given_ANewContent_when_TrytoCreateANewContent_then_somethingFails() {

    Mockito.doThrow(new RuntimeException("error: Id already exist"))
            .when(repository)
            .save(Mockito.any(CatalogContent.class));

    long expectedContentId = 1;
    long expectedStreamId = 1;
    String expectedTitle = "title";
    Set<String> expectedTags = Set.of("tag1", "tag2");

    ContentServiceResponse response = service.createContent(expectedContentId,
            expectedStreamId,
            expectedTitle,
            expectedTags);

    Assertions.assertThat(response).isInstanceOf(ContentServiceResponseFailure.class);

  }

  @Test
  public void when_TryToCreateAnAlreadyExistingContent_then_ShouldFail() {

    long expectedContentId = 1;
    long expectedStreamId = 1;
    String expectedTitle = "title";
    String[] expectedTags = new String[]{"tag1", "tag2"};

    //given
    Mockito.doNothing()
            .when(repository)
            .save(Mockito.any(CatalogContent.class));
    Mockito.doReturn(CatalogContent.builder()
            .contentId(expectedContentId)
            .streamId(expectedStreamId)
            .title(expectedTitle)
            .tags(Set.of(expectedTags))
            .build()
    ).when(repository).findById(expectedContentId);


    ContentServiceResponse response = service.createContent(expectedContentId,
            expectedStreamId,
            expectedTitle,
            Set.of(expectedTags));

    Assertions.assertThat(response).isInstanceOf(ContentServiceResponseFailure.class);

  }
}

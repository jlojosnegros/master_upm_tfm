package org.jlom.master_upm.tfm.micronaut.catalog.model;

import io.micronaut.test.annotation.MicronautTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

@MicronautTest
public class ModelTest {
  private static final Logger LOG = LoggerFactory.getLogger(ModelTest.class);


  @Inject
  private CatalogContentRepository repository;


  @Test
  public void findWithTags() {
    final ContentStatus status = ContentStatus.SOON;
    final Date available = Date.from(Instant.now());


    CatalogContent content_odd= CatalogContent.builder()
            .contentId(1)
            .streamId(1)
            .title("odd")
            .status(status)
            .available(available)
            .tags(Set.of("tag_1","tag_3","tag_5"))
            .build();

    CatalogContent content_even= CatalogContent.builder()
            .contentId(2)
            .streamId(2)
            .title("even")
            .status(status)
            .available(available)
            .tags(Set.of("tag_2","tag_4"))
            .build();

    CatalogContent content_first= CatalogContent.builder()
            .contentId(3)
            .streamId(3)
            .title("first")
            .status(status)
            .available(available)
            .tags(Set.of("tag_1","tag_2","tag_3"))
            .build();

    CatalogContent content_last= CatalogContent.builder()
            .contentId(4)
            .streamId(4)
            .title("last")
            .status(status)
            .available(available)
            .tags(Set.of("tag_5","tag_4"))
            .build();

    repository.save(content_even);
    repository.save(content_odd);
    repository.save(content_first);
    repository.save(content_last);

    List<CatalogContent> tag_1 = repository.findWithExactlyTags(Set.of("tag_1"));
    LOG.error("jlom: with tag_1: " + tag_1);
    Assertions.assertThat(tag_1).containsExactlyInAnyOrder(content_odd,content_first);
  }

}

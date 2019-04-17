package org.jlom.master_upm.tfm.springboot.catalog.model;

import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface ContentQueriesRepository {
  CatalogContent findById(long contentId);
  List<CatalogContent> findWithExactlyTags(Set<String> tags);
  CatalogContent findByStreamId(long streamId);
  List<CatalogContent> findAvailableAfter(ZonedDateTime datetime);
  Collection<CatalogContent> findAll();
}

package org.jlom.master_upm.tfm.springboot.catalog.model;

import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ContentQueriesRepository {
  CatalogContent findById(long contentId);
  List<CatalogContent> findWithExactlyTags(Set<String> tags);
  CatalogContent findByStreamId(long streamId);
  List<CatalogContent> findAvailableAfter(DateTime datetime);
  Collection<CatalogContent> findAll();
}

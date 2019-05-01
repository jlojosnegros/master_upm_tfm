package org.jlom.master_upm.tfm.springboot.catalog.model.api;

import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.springboot.catalog.model.ContentStatus;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface ContentQueriesRepository {
  CatalogContent findById(long contentId);
  List<CatalogContent> findWithExactlyTags(Set<String> tags);
  CatalogContent findByStreamId(long streamId);
  List<CatalogContent> findAvailableAfter(Date date);
  Collection<CatalogContent> findAll();
  List<CatalogContent> findWithStatus(ContentStatus contentStatus);
}

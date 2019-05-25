package org.jlom.master_upm.tfm.graalvm.catalog.model.api;

import org.jlom.master_upm.tfm.graalvm.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.graalvm.catalog.model.ContentStatus;

import java.util.Date;
import java.util.List;
import java.util.Set;


public interface CatalogQueriesRepository {
  CatalogContent findById(long contentId);
  List<CatalogContent> findWithExactlyTags(Set<String> tags);
  CatalogContent findByStreamId(long streamId);
  List<CatalogContent> findAvailableAfter(Date date);
  List<CatalogContent> findAll();
  List<CatalogContent> findWithStatus(ContentStatus contentStatus);
}

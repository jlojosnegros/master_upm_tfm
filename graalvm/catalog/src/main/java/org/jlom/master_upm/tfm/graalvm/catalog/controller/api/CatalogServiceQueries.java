package org.jlom.master_upm.tfm.graalvm.catalog.controller.api;

import org.jlom.master_upm.tfm.graalvm.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.graalvm.catalog.model.ContentStatus;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface CatalogServiceQueries {

  CatalogContent getContent(long contentId);
  List<CatalogContent> getContentsWithTags(Set<String> tags);
  List<CatalogContent> getContentWithStatus(ContentStatus contentStatus);
  CatalogContent getContentWithStream(long streamId);
  List<CatalogContent> getAvailableAfter(Date date);
  List<CatalogContent> listAll();
}

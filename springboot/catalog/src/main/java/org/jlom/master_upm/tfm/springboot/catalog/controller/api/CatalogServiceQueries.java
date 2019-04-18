package org.jlom.master_upm.tfm.springboot.catalog.controller.api;

import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface CatalogServiceQueries {
  CatalogContent getContent(long contentId);
  List<CatalogContent> getContentsWithTags(Set<String> tags);
  CatalogContent getContentWithStream(long streamId);
  List<CatalogContent> getAvailableAfter(Date date);
  List<CatalogContent> listAll();
}

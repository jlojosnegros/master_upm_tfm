package org.jlom.master_upm.tfm.springboot.catalog.model;

import java.util.Set;

public interface ContentCommandsRepository {

  void save (CatalogContent content);
  void update(CatalogContent content);
  void delete(long contentId);
  void changeStatus(long contentId, ContentStatus status);
  void addTags(long contentId, Set<String> tags);

}

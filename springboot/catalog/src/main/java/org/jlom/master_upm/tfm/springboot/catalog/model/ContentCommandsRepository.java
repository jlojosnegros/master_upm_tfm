package org.jlom.master_upm.tfm.springboot.catalog.model;

import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ContentCommandsRepository {

  void save (CatalogContent content);
  void update(CatalogContent content);
  void delete(long contentId);
  void changeStatus(long contentId, ContentStatus status);
  void addTags(long contentId, Set<String> tags);

}

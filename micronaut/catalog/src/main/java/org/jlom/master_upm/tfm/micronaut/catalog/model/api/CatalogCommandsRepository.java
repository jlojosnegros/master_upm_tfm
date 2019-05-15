package org.jlom.master_upm.tfm.micronaut.catalog.model.api;

import org.jlom.master_upm.tfm.micronaut.catalog.model.CatalogContent;

public interface CatalogCommandsRepository {
  void save (CatalogContent content);
  Long delete(long contentId);
}

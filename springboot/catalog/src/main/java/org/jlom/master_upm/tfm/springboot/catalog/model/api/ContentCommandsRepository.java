package org.jlom.master_upm.tfm.springboot.catalog.model.api;

import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentCommandsRepository {

  void save (CatalogContent content);
  void update(CatalogContent content);
  Long delete(long contentId);


}

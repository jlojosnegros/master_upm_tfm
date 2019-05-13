package org.jlom.master_upm.tfm.micronaut.catalog.model.api;

import org.jlom.master_upm.tfm.micronaut.catalog.model.CatalogContent;

import java.util.List;


public interface CatalogQueriesRepository {
  List<CatalogContent> findAll();
}

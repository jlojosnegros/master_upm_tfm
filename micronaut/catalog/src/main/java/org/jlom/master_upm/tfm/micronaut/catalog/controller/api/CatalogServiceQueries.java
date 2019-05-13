package org.jlom.master_upm.tfm.micronaut.catalog.controller.api;

import org.jlom.master_upm.tfm.micronaut.catalog.model.CatalogContent;

import java.util.List;

public interface CatalogServiceQueries {

  List<CatalogContent> listAll();
}

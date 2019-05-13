package org.jlom.master_upm.tfm.micronaut.catalog.controller;

import org.jlom.master_upm.tfm.micronaut.catalog.controller.api.CatalogServiceQueries;
import org.jlom.master_upm.tfm.micronaut.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.micronaut.catalog.model.CatalogContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;

@Singleton
public class CatalogContentService implements CatalogServiceQueries {

  private static Logger LOG = LoggerFactory.getLogger(CatalogContentService.class);


  private CatalogContentRepository repository;

  public CatalogContentService(CatalogContentRepository repository) {
    this.repository = repository;
  }


  @Override
  public List<CatalogContent> listAll() {
    return repository.findAll();
  }
}

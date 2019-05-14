package org.jlom.master_upm.tfm.micronaut.catalog.controller;

import org.jlom.master_upm.tfm.micronaut.catalog.controller.api.CatalogServiceQueries;
import org.jlom.master_upm.tfm.micronaut.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.micronaut.catalog.model.CatalogContentRepository;
import org.jlom.master_upm.tfm.micronaut.catalog.model.ContentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Singleton
public class CatalogContentService implements CatalogServiceQueries {

  private static Logger LOG = LoggerFactory.getLogger(CatalogContentService.class);


  private CatalogContentRepository repository;

  public CatalogContentService(CatalogContentRepository repository) {
    this.repository = repository;
  }


  @Override
  public CatalogContent getContent(long contentId) {
    return repository.findById(contentId);
  }

  @Override
  public List<CatalogContent> getContentsWithTags(Set<String> tags) {
    LOG.info("Service:getContentsWithTags:  " + tags);
    return repository.findWithExactlyTags(tags);
  }

  @Override
  public List<CatalogContent> getContentWithStatus(ContentStatus contentStatus) {
    return null;
  }

  @Override
  public CatalogContent getContentWithStream(long streamId) {
    return null;
  }

  @Override
  public List<CatalogContent> getAvailableAfter(Date date) {
    return null;
  }

  @Override
  public List<CatalogContent> listAll() {
    return repository.findAll();
  }
}
